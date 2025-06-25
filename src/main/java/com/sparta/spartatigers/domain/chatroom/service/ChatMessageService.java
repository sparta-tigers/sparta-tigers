package com.sparta.spartatigers.domain.chatroom.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

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

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final DirectRoomRepository directRoomRepository;
    private final UserRepository userRepository;
    private final DirectMessageRepository directMessageRepository;
    private final RedisDirectMessagePublisher redisPublisher;

    @Transactional
    public void sendMessage(Long senderId, ChatMessageRequest request) {
        Long roomId = request.getRoomId();
        String messageText = request.getMessage();

        DirectRoom room =
                directRoomRepository
                        .findById(roomId)
                        .orElseThrow(
                                () ->
                                        new InvalidRequestException(
                                                ExceptionCode.CHATROOM_NOT_FOUND));

        User sender =
                userRepository
                        .findById(senderId)
                        .orElseThrow(
                                () -> new InvalidRequestException(ExceptionCode.USER_NOT_FOUND));

        // db에 메시지 저장
        DirectMessage savedMessage =
                directMessageRepository.save(
                        new DirectMessage(room, sender, messageText, LocalDateTime.now()));

        // redis 발행
        redisPublisher.publish("directRoom:" + roomId, RedisMessage.from(savedMessage));
    }
}
