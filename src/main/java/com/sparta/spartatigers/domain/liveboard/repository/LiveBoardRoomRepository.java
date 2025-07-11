package com.sparta.spartatigers.domain.liveboard.repository;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoardRoom;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LiveBoardRoomRepository {

    private static final String LIVEBOARD_ROOMS = "liveboard:rooms";
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private HashOperations<String, String, LiveBoardRoom> opsHash;

    @PostConstruct
    private void init() {
        opsHash = redisTemplate.opsForHash();
    }

    public void saveRoom(LiveBoardRoom room) {
        opsHash.put(LIVEBOARD_ROOMS, room.getRoomId(), room);
    }

    public LiveBoardRoom findRoomById(String roomId) {
        Object object = opsHash.get(LIVEBOARD_ROOMS, roomId);
        LiveBoardRoom room = objectMapper.convertValue(object, LiveBoardRoom.class);
        return room;
    }

    public List<LiveBoardRoom> findAllRoom() {
        List<LiveBoardRoom> rooms = new ArrayList<>();
        for (Object room : opsHash.values(LIVEBOARD_ROOMS)) {
            rooms.add(objectMapper.convertValue(room, LiveBoardRoom.class));
        }

        return rooms;
    }

    public void deleteRoom(String roomId) {
        opsHash.delete(LIVEBOARD_ROOMS, roomId);
    }

    public boolean existsById(String roomId) {
        return opsHash.hasKey(LIVEBOARD_ROOMS, roomId);
    }
}
