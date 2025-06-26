package com.sparta.spartatigers.domain.chatroom.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.registry.RedisUserSessionRegistry;
import com.sparta.spartatigers.domain.chatroom.repository.DirectRoomRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;

@Service
@RequiredArgsConstructor
public class UserConnectService {

    private final RedisUserSessionRegistry userSessionRegistry;
    private final DirectRoomRepository directRoomRepository;

    // 채팅방 목록 조회용
    public boolean isUserOnline(Long requesterId, Long targetUserId) {
        if (!hasChatRoomBetween(requesterId, targetUserId)) {
            throw new InvalidRequestException(ExceptionCode.ACCESS_DENIED);
        }

        return userSessionRegistry.isUserConnected(targetUserId);
    }

    // user A와 B 사이에 채팅방이 존재하는지 확인하는 로직
    // 채팅방 목록 조회용
    private boolean hasChatRoomBetween(Long userAId, Long userBId) {
        return directRoomRepository.existsBySenderIdAndReceiverId(userAId, userBId)
                || directRoomRepository.existsBySenderIdAndReceiverId(userBId, userAId);
    }

    // 특정 채팅방 참여중인 상대방 유저 접속 여부 조회
    public boolean isOpponentOnlineInRoom(Long requesterId, Long roomId) {
        var room =
                directRoomRepository
                        .findById(roomId)
                        .orElseThrow(
                                () ->
                                        new InvalidRequestException(
                                                ExceptionCode.CHATROOM_NOT_FOUND));

        Long opponentId;

        if (requesterId.equals(room.getSender().getId())) {
            opponentId = room.getReceiver().getId();
        } else if (requesterId.equals(room.getReceiver().getId())) {
            opponentId = room.getSender().getId();
        } else {
            throw new InvalidRequestException(ExceptionCode.ACCESS_DENIED);
        }

        return userSessionRegistry.isUserConnected(opponentId);
    }
}
