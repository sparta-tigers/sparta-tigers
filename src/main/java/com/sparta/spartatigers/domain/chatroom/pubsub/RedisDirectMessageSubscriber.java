package com.sparta.spartatigers.domain.chatroom.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.spartatigers.domain.chatroom.dto.request.ChatMessageRequest;
import com.sparta.spartatigers.domain.chatroom.dto.response.ChatMessageResponse;
import com.sparta.spartatigers.domain.chatroom.model.entity.DirectMessage;
import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;
import com.sparta.spartatigers.domain.chatroom.repository.DirectMessageRepository;
import com.sparta.spartatigers.domain.chatroom.repository.DirectRoomRepository;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisDirectMessageSubscriber implements MessageListener {

	private final SimpMessagingTemplate messagingTemplate;
	private final DirectMessageRepository messageRepository;
	private final UserRepository userRepository;
	private final DirectRoomRepository roomRepository;

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			String body = new String(message.getBody(), StandardCharsets.UTF_8);
			ChatMessageRequest request = new ObjectMapper().readValue(body, ChatMessageRequest.class);

			// 메시지 저장
			DirectRoom room = roomRepository.findById(request.getRoomId()).orElseThrow();
			User sender = userRepository.findById(request.getSenderId()).orElseThrow();

			DirectMessage savedMessage = messageRepository.save(
				new DirectMessage(room, sender, request.getMessage(), LocalDateTime.now())
			);

			// STOMP 대상 경로로 전송
			ChatMessageResponse response = new ChatMessageResponse(
				savedMessage.getDirectRoom().getId(),
				savedMessage.getSender().getId(),
				savedMessage.getSender().getNickname(),
				savedMessage.getMessage(),
				savedMessage.getSentAt()
			);

			messagingTemplate.convertAndSend("/topic/chatroom/" + response.getRoomId(), response);

		} catch (Exception e) {
			log.error("메시지 처리 중 오류 발생", e);
		}
	}
}

