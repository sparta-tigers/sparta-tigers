package com.sparta.spartatigers.domain.chatroom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.chatroom.dto.response.DirectRoomMessageResponse;
import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;
import com.sparta.spartatigers.domain.chatroom.repository.DirectMessageRepository;
import com.sparta.spartatigers.domain.chatroom.repository.DirectRoomRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectMessageService {

    private final DirectRoomRepository directRoomRepository;
    private final DirectMessageRepository directRoomMessageRepository;

    public Page<DirectRoomMessageResponse> getMessages(
            Long roomId, Long userId, Pageable pageable) {
        log.info("[getMessages] 메시지 목록 조회 시작 - roomId: {}, userId: {}", roomId, userId);
        DirectRoom room =
                directRoomRepository
                        .findById(roomId)
                        .orElseThrow(
                                () -> {
                                    log.warn("[getMessages] 채팅방 없음 - roomId: {}", roomId);
                                    return new InvalidRequestException(
                                            ExceptionCode.CHATROOM_NOT_FOUND);
                                });

        if (!room.getSender().getId().equals(userId)
                && !room.getReceiver().getId().equals(userId)) {
            log.warn("[getMessages] 권한 없는 유저의 접근 시도 - roomId: {}, userId: {}", roomId, userId);
            throw new InvalidRequestException(ExceptionCode.FORBIDDEN_REQUEST);
        }

        Page<DirectRoomMessageResponse> messages =
                directRoomMessageRepository
                        .findByDirectRoomIdWithSender(roomId, pageable)
                        .map(DirectRoomMessageResponse::from);

        log.info(
                "[getMessages] 메시지 조회 완료 - roomId: {}, 총 개수: {}",
                roomId,
                messages.getTotalElements());
        return messages;
    }
}
