package com.sparta.spartatigers.domain.chatroom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.dto.response.DirectRoomMessageResponse;
import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;
import com.sparta.spartatigers.domain.chatroom.repository.DirectMessageRepository;
import com.sparta.spartatigers.domain.chatroom.repository.DirectRoomRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;

@Service
@RequiredArgsConstructor
public class DirectMessageService {

    private final DirectRoomRepository directRoomRepository;
    private final DirectMessageRepository directRoomMessageRepository;

    public Page<DirectRoomMessageResponse> getMessages(
            Long roomId, Long userId, Pageable pageable) {
        DirectRoom room =
                directRoomRepository
                        .findById(roomId)
                        .orElseThrow(
                                () ->
                                        new InvalidRequestException(
                                                ExceptionCode.CHATROOM_NOT_FOUND));

        if (!room.getSender().getId().equals(userId)
                && !room.getReceiver().getId().equals(userId)) {
            throw new InvalidRequestException(ExceptionCode.FORBIDDEN_REQUEST);
        }

        return directRoomMessageRepository
                .findByDirectRoomIdWithSender(roomId, pageable)
                .map(DirectRoomMessageResponse::from);
    }
}
