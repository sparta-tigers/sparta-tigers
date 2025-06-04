package com.sparta.spartatigers.domain.chatroom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.dto.response.DirectRoomDto;
import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;
import com.sparta.spartatigers.domain.chatroom.repository.DirectRoomRepository;
import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;
import com.sparta.spartatigers.domain.exchangerequest.repository.ExchangeRequestRepository;
import com.sparta.spartatigers.domain.user.model.entity.User;

@Service
@RequiredArgsConstructor
public class DirectRoomService {

    private final ExchangeRequestRepository exchangeRequestRepository;
    private final DirectRoomRepository directRoomRepository;

    @Transactional
    public DirectRoomDto createRoom(Long exchangeRequestId) {
        ExchangeRequest exchangeRequest =
                exchangeRequestRepository.findByIdOrElseThrow(exchangeRequestId);
        DirectRoom room =
                directRoomRepository
                        .findByExchangeRequest(exchangeRequest)
                        .orElseGet(
                                () ->
                                        directRoomRepository.save(
                                                DirectRoom.create(exchangeRequest)));
        return DirectRoomDto.from(room);
    }

    @Transactional(readOnly = true)
    public Page<DirectRoomDto> getRoomsForUser(User currentUser, Pageable pageable) {
        return directRoomRepository.findAllByUser(currentUser, pageable).map(DirectRoomDto::from);
    }
}
