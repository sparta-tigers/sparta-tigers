package com.sparta.spartatigers.domain.chatroom.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.chatroom.dto.request.ChatMessageRequest;
import com.sparta.spartatigers.domain.chatroom.dto.response.RedisMessage;
import com.sparta.spartatigers.domain.chatroom.model.entity.DirectMessage;
import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;
import com.sparta.spartatigers.domain.chatroom.pubsub.RedisDirectMessagePublisher;
import com.sparta.spartatigers.domain.chatroom.repository.DirectMessageRepository;
import com.sparta.spartatigers.domain.chatroom.repository.DirectRoomRepository;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.domain.user.repository.UserRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;
import com.sparta.spartatigers.global.util.RedisRateLimiter;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    // TODO: 운영 단계에서는 조정
    private static final int MESSAGE_LIMIT = 3;
    private static final Duration LIMIT_DURATION = Duration.ofSeconds(2);

    private final DirectRoomRepository directRoomRepository;
    private final UserRepository userRepository;
    private final DirectMessageRepository directMessageRepository;
    private final RedisDirectMessagePublisher redisPublisher;
    private final RedisRateLimiter redisRateLimiter;

    @Transactional
    public void sendMessage(Long senderId, ChatMessageRequest request) {
        log.info(
                "[sendMessage] 메시지 전송 요청 - senderId: {}, roomId: {}",
                senderId,
                request.getRoomId());

        String rateLimitKey = "rate-limit:user:" + senderId;
        boolean isLimited =
                redisRateLimiter.isRateLimited(rateLimitKey, MESSAGE_LIMIT, LIMIT_DURATION);
        if (isLimited) {
            log.warn("[sendMessage] 메시지 전송 레이트 리밋 초과 - senderId: {}", senderId);
            throw new InvalidRequestException(ExceptionCode.TOO_MANY_MESSAGE);
        }

        Long roomId = request.getRoomId();
        String messageText = request.getMessage();

        DirectRoom room =
                directRoomRepository
                        .findById(roomId)
                        .orElseThrow(
                                () -> {
                                    log.warn("[sendMessage] 채팅방 없음 - roomId: {}", roomId);
                                    return new InvalidRequestException(
                                            ExceptionCode.CHATROOM_NOT_FOUND);
                                });

        User sender =
                userRepository
                        .findById(senderId)
                        .orElseThrow(
                                () -> {
                                    log.warn("[sendMessage] 사용자 없음 - senderId: {}", senderId);
                                    return new InvalidRequestException(
                                            ExceptionCode.USER_NOT_FOUND);
                                });

        // db에 메시지 저장
        DirectMessage savedMessage =
                directMessageRepository.save(
                        new DirectMessage(room, sender, messageText, LocalDateTime.now()));
        log.debug(
                "[sendMessage] 메시지 저장 완료 - messageId: {}, senderId: {}, roomId: {}",
                savedMessage.getId(),
                senderId,
                roomId);

        // redis 발행
        redisPublisher.publish("directRoom:" + roomId, RedisMessage.from(savedMessage));
        log.info("[sendMessage] Redis 메시지 발행 완료 - roomId: {}", roomId);
    }
}
