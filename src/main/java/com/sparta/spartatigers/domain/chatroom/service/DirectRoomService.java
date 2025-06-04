package com.sparta.spartatigers.domain.chatroom.service;

import com.sparta.spartatigers.domain.chatroom.dto.response.DirectRoomDto;
import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;
import com.sparta.spartatigers.domain.chatroom.repository.DirectRoomRepository;
import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;
import com.sparta.spartatigers.domain.exchangerequest.repository.ExchangeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DirectRoomService {

	private final ExchangeRequestRepository exchangeRequestRepository;
	private final DirectRoomRepository directRoomRepository;

	@Transactional
	public DirectRoomDto createRoom(Long exchangeRequestId, Long currentUserId) {
		ExchangeRequest exchangeRequest = exchangeRequestRepository.findByIdOrElseThrow(exchangeRequestId);

		DirectRoom room = directRoomRepository.findByExchangeRequest(exchangeRequest)
			.orElseGet(() -> directRoomRepository.save(DirectRoom.create(exchangeRequest)));

		return DirectRoomDto.from(room, currentUserId);
	}

	@Transactional(readOnly = true)
	public Page<DirectRoomDto> getRoomsForUser(Long userId, Pageable pageable) {
		Page<DirectRoom> rooms = directRoomRepository.findAllBySenderIdOrReceiverId(userId, userId, pageable);
		return rooms.map(room -> DirectRoomDto.from(room, userId));
	}

}
