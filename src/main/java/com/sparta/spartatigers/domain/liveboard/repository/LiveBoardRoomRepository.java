package com.sparta.spartatigers.domain.liveboard.repository;

import java.util.List;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoardRoom;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LiveBoardRoomRepository {

	private final RedisTemplate<String, Object> redisTemplate;
	private HashOperations<String, String, LiveBoardRoom> opsHash;

	private static final String LIVEBOARD_ROOMS = "liveboard:rooms";

	@PostConstruct
	private void init() {
		opsHash = redisTemplate.opsForHash();
	}

	public void saveRoom(LiveBoardRoom room) {
		opsHash.put(LIVEBOARD_ROOMS, room.getRoomId(), room);
	}

	public LiveBoardRoom findRoomById(String roomId) {
		return opsHash.get(LIVEBOARD_ROOMS, roomId);
	}

	public List<LiveBoardRoom> findAllRoom() {
		return opsHash.values(LIVEBOARD_ROOMS);
	}

	public void deleteRoom(String roomId) {
		opsHash.delete(LIVEBOARD_ROOMS, roomId);
	}

}
