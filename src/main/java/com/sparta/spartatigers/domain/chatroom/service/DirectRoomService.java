package com.sparta.spartatigers.domain.chatroom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.dto.response.DirectRoomResponseDto;
import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;
import com.sparta.spartatigers.domain.chatroom.repository.DirectMessageRepository;
import com.sparta.spartatigers.domain.chatroom.repository.DirectRoomRepository;
import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;
import com.sparta.spartatigers.domain.exchangerequest.repository.ExchangeRequestRepository;
import com.sparta.spartatigers.domain.user.model.entity.User;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;

@Service
@RequiredArgsConstructor
public class DirectRoomService {

    private final ExchangeRequestRepository exchangeRequestRepository;
    private final DirectRoomRepository directRoomRepository;
    private final DirectMessageRepository directMessageRepository;

    @Transactional
    public DirectRoomResponseDto createRoom(Long exchangeRequestId, Long currentUserId) {
        ExchangeRequest exchangeRequest =
                exchangeRequestRepository.findByIdOrElseThrow(exchangeRequestId);

        // 권한 확인: 요청한 사람이 교환 요청의 sender 또는 receiver여야 함
        if (!exchangeRequest.getSender().getId().equals(currentUserId)
                && !exchangeRequest.getReceiver().getId().equals(currentUserId)) {
            throw new InvalidRequestException(ExceptionCode.UNAUTHORIZED);
        }

        // sender/receiver는 교환 요청 그대로
        User sender = exchangeRequest.getSender();
        User receiver = exchangeRequest.getReceiver();

        DirectRoom room =
                directRoomRepository
                        .findByExchangeRequest(exchangeRequest)
                        .orElseGet(
                                () ->
                                        directRoomRepository.save(
                                                DirectRoom.create(
                                                        exchangeRequest, sender, receiver)));

        return DirectRoomResponseDto.from(room);
    }

    public Page<DirectRoomResponseDto> getRoomsForUser(Long currentUserId, Pageable pageable) {
        return directRoomRepository
                .findBySenderIdOrReceiverIdWithUsers(currentUserId, pageable)
                .map(DirectRoomResponseDto::from);
    }

    // 유저가 직접 채팅방을 삭제할 수도 있음
    // TODO: 교환 완료 시 채팅방이 readOnly 상태로 바뀌고 6시간 후 자동 삭제 (확장 기능)
    @Transactional
    public void deleteRoom(Long directRoomId, Long currentUserId) {
        DirectRoom room =
                directRoomRepository
                        .findById(directRoomId)
                        .orElseThrow(
                                () ->
                                        new InvalidRequestException(
                                                ExceptionCode.CHATROOM_NOT_FOUND));

        boolean isSender = room.getSender().getId().equals(currentUserId);
        boolean isReceiver = room.getReceiver().getId().equals(currentUserId);

        if (!isSender && !isReceiver) {
            throw new InvalidRequestException(ExceptionCode.FORBIDDEN_REQUEST);
        }

        directMessageRepository.deleteAllByDirectRoomId(room.getId());
        directRoomRepository.delete(room);
    }

    // 교환 완료 시 호출되며 채팅방이 삭제되는 로직
    @Transactional
    public void deleteRoomByExchangeRequestId(Long exchangeRequestId) {
        DirectRoom room =
                directRoomRepository
                        .findByExchangeRequestId(exchangeRequestId)
                        .orElseThrow(
                                () ->
                                        new InvalidRequestException(
                                                ExceptionCode.CHATROOM_NOT_FOUND));

        directMessageRepository.deleteAllByDirectRoomId(room.getId());
        directRoomRepository.delete(room);
    }
}
