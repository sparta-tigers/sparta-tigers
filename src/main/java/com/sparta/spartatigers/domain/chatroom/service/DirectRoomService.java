package com.sparta.spartatigers.domain.chatroom.service;

import com.sparta.spartatigers.domain.chatroom.dto.response.DirectRoomResponseDto;
import com.sparta.spartatigers.domain.chatroom.model.entity.DirectRoom;
import com.sparta.spartatigers.domain.chatroom.repository.DirectRoomRepository;
import com.sparta.spartatigers.domain.exchangerequest.model.entity.ExchangeRequest;
import com.sparta.spartatigers.domain.exchangerequest.repository.ExchangeRequestRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.ServerException;
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
	public DirectRoomResponseDto createRoom(Long exchangeRequestId, Long currentUserId) {
		ExchangeRequest exchangeRequest =
			exchangeRequestRepository.findByIdOrElseThrow(exchangeRequestId);

		DirectRoom room =
			directRoomRepository
				.findByExchangeRequest(exchangeRequest)
				.orElseGet(
					() ->
						directRoomRepository.save(
							DirectRoom.create(exchangeRequest)));

		return DirectRoomResponseDto.from(room, currentUserId);
	}

	@Transactional(readOnly = true)
	public Page<DirectRoomResponseDto> getRoomsForUser(Long userId, Pageable pageable) {
		Page<DirectRoom> rooms =
			directRoomRepository.findAllBySenderIdOrReceiverId(userId, userId, pageable);
		return rooms.map(room -> DirectRoomResponseDto.from(room, userId));
	}

	@Transactional
	public void deleteRoom(Long directRoomId, Long currentUserId) {
		DirectRoom room =
			directRoomRepository
				.findById(directRoomId)
				.orElseThrow(() -> new ServerException(ExceptionCode.CHATROOM_NOT_FOUND));

		boolean isSender = room.getSender().getId().equals(currentUserId);
		boolean isReceiver = room.getReceiver().getId().equals(currentUserId);

		if (!isSender && !isReceiver) {
			throw new ServerException(ExceptionCode.FORBIDDEN_REQUEST); // 403 권한 없음
		}

		directRoomRepository.delete(room);
	}
}
