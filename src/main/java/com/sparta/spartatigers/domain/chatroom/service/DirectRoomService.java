package com.sparta.spartatigers.domain.chatroom.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.chatroom.dto.response.DirectRoomDto;
import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;
import com.sparta.spartatigers.domain.chatroom.repository.DirectRoomRepository;
import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;
import com.sparta.spartatigers.domain.exchangerequest.repository.ExchangeRequestRepository;

@Service
@RequiredArgsConstructor
public class DirectRoomService {

    private final ExchangeRequestRepository exchangeRequestRepository;
    private final DirectRoomRepository directRoomRepository;

    @Transactional
    public DirectRoomDto createRoom(Long exchangeRequestId) {
        ExchangeRequest exchangeRequest =
                exchangeRequestRepository
                        .findById(exchangeRequestId)
                        .orElseThrow(() -> new IllegalArgumentException("교환 요청을 찾을 수 없습니다."));

        Optional<DirectRoom> existingRoom =
                directRoomRepository.findByExchangeRequest(exchangeRequest);
        DirectRoom room =
                existingRoom.orElseGet(
                        () -> directRoomRepository.save(DirectRoom.create(exchangeRequest)));
        return DirectRoomDto.from(room);
    }
}
