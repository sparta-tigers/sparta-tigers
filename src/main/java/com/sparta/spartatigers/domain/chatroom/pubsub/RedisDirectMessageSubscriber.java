package com.sparta.spartatigers.domain.chatroom.pubsub;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.chatroom.config.RedisUserSessionRegistry;
import com.sparta.spartatigers.domain.chatroom.dto.response.ChatMessageResponse;
import com.sparta.spartatigers.domain.chatroom.model.entity.DirectMessage;
import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;
import com.sparta.spartatigers.domain.chatroom.repository.DirectMessageRepository;
import com.sparta.spartatigers.domain.chatroom.repository.DirectRoomRepository;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.repository.UserRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisDirectMessageSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final DirectMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final DirectRoomRepository roomRepository;
    private final RedisUserSessionRegistry userSessionRegistry;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("onMessage 호출됨ㅎㅇ");
        try {
            // Redis에서 전달된 메시지를 UTF-8 문자열로 변환
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> payload = mapper.readValue(body, Map.class);

            Long senderId = Long.parseLong(payload.get("senderId").toString());
            Long roomId = Long.parseLong(payload.get("roomId").toString());
            String messageText = payload.get("message").toString();

            DirectRoom room =
                    roomRepository
                            .findById(roomId)
                            .orElseThrow(
                                    () -> new ServerException(ExceptionCode.CHATROOM_NOT_FOUND));
            User sender =
                    userRepository
                            .findById(senderId)
                            .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));

            // 수신자 결정 (sender와 반대편 유저)
            User receiver =
                    room.getSender().getId().equals(senderId)
                            ? room.getReceiver()
                            : room.getSender();
            Long receiverId = receiver.getId();

            boolean isConnectedHere = userSessionRegistry.isUserConnected(receiverId);

            if (!isConnectedHere) {
                log.info("Receiver {} is not connected here. Skipping.", receiverId);
                return;
            }

            DirectMessage savedMessage =
                    messageRepository.save(
                            new DirectMessage(room, sender, messageText, LocalDateTime.now()));

            ChatMessageResponse response =
                    new ChatMessageResponse(
                            roomId,
                            senderId,
                            sender.getNickname(),
                            messageText,
                            savedMessage.getSentAt());

            messagingTemplate.convertAndSend("/server/directRoom/" + roomId, response);
            log.info("Redis 메시지 수신 처리 완료");

        } catch (Exception e) {
            log.error("RedisDirectMessageSubscriber 오류 발생", e);
        }
    }
}
