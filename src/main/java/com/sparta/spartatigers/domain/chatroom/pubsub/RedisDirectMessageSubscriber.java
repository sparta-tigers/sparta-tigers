package com.sparta.spartatigers.domain.chatroom.pubsub;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.chatroom.dto.request.ChatMessageRequest;
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
    private final SimpUserRegistry simpUserRegistry;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("✅ RedisDirectMessageSubscriber.onMessage 호출됨");
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            // json 역직렬화 메시지를 -> 객체로 전환
            ChatMessageRequest request =
                    new ObjectMapper().readValue(body, ChatMessageRequest.class);

            String realReceiverId = String.valueOf(request.getReceiverId());

            // 현재 서버에 대상 유저가 연결되어 있는지 확인
            boolean isConnectedHere =
                    simpUserRegistry.getUsers().stream()
                            .anyMatch(user -> user.getName().equals(realReceiverId));

            if (!isConnectedHere) {
                log.info(
                        "Receiver {} is not connected to this server. Skipping message processing.",
                        realReceiverId);
                return; // 이 서버는 DB 저장 및 전송 안 함
            }

            DirectRoom room =
                    roomRepository
                            .findById(request.getRoomId())
                            .orElseThrow(
                                    () -> new ServerException(ExceptionCode.CHATROOM_NOT_FOUND));
            User sender =
                    userRepository
                            .findById(request.getSenderId())
                            .orElseThrow(() -> new ServerException(ExceptionCode.USER_NOT_FOUND));

            log.info("💾 DirectMessage 저장 준비");
            // 메시지 저장
            // TODO: dto에서 처리하고 호출
            // TODO: SimpUserRegistry에 Websocket 커넥션이 들어있는지 확인<<
            DirectMessage savedMessage =
                    messageRepository.save(
                            new DirectMessage(
                                    room, sender, request.getMessage(), LocalDateTime.now()));

            // STOMP 대상 경로로 전송하는 로직인데 TODO: dto에서 처리하고 호출
            ChatMessageResponse response =
                    new ChatMessageResponse(
                            savedMessage.getDirectRoom().getId(),
                            savedMessage.getSender().getId(),
                            savedMessage.getSender().getNickname(),
                            savedMessage.getMessage(),
                            savedMessage.getSentAt());
            log.info("💬 STOMP 전송 준비: /server/directRoom/{}", response.getRoomId());

            messagingTemplate.convertAndSend(
                    "/server/directRoom/" + response.getRoomId(), response);

            log.info("📥 Redis 메시지 수신됨: {}", request);
            log.info("📌 수신자 연결 여부: {}", isConnectedHere);

        } catch (Exception e) {
            log.error("레전드 버그 발생", e);
        }
    }
}
