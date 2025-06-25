package com.sparta.spartatigers.domain.liveboard.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.liveboard.dto.response.LiveBoardRoomResponseDto;
import com.sparta.spartatigers.domain.liveboard.model.LiveBoardRoom;
import com.sparta.spartatigers.domain.liveboard.repository.LiveBoardConnectionRepository;
import com.sparta.spartatigers.domain.liveboard.repository.LiveBoardRoomRepository;
import com.sparta.spartatigers.domain.match.model.entity.Match;
import com.sparta.spartatigers.domain.match.repository.MatchRepository;

@Service
@RequiredArgsConstructor
public class LiveBoardRoomService {

    private final RedisTemplate<String, String> redisTemplate;
    private final LiveBoardRoomRepository roomRepository;
    private final LiveBoardConnectionRepository connectionRepository;
    private final MatchRepository matchRepository; // TODO: 경기일정 크롤러 확인하기 + 스케줄러

    // 라이브 보드룸 생성
    public String createTodayRoom() {

        // 오늘 경기 일정 찾기
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Match> matches = matchRepository.findAllByMatchTimeBetween(start, end);

        boolean alreadyCreated = false;

        // 라이브 보드룸 생성 후 저장
        for (Match match : matches) {
            String roomId = "ROOM_" + match.getId();

            if (roomRepository.existsById(roomId)) { // 중복 여부 확인 (생성 막지는 않음)
                alreadyCreated = true;
            }

            String title = match.getAwayTeam().getName() + "VS" + match.getHomeTeam().getName();
            LocalDateTime matchTime = match.getMatchTime();

            LiveBoardRoom room = LiveBoardRoom.of(roomId, match.getId(), title, matchTime);

            roomRepository.saveRoom(room);
        }

        return alreadyCreated ? "ALREADY_CREATED" : "CREATED";
    }

    // 라이브 보드룸 전체 조회
    public List<LiveBoardRoomResponseDto> findAllRooms() {
        return roomRepository.findAllRoom().stream()
                .map(
                        room -> {
                            Long count = connectionRepository.getConnectionCount(room.getRoomId());
                            return LiveBoardRoomResponseDto.of(room, count);
                        })
                .collect((Collectors.toList()));
    }

    // 오늘의 라이브 보드룸 조회
    public List<LiveBoardRoomResponseDto> findTodayRooms() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return roomRepository.findAllRoom().stream()
                .filter(room -> !room.getOpenAt().isBefore(start) && room.getOpenAt().isBefore(end))
                .map(
                        room -> {
                            Long count = connectionRepository.getConnectionCount(room.getRoomId());
                            return LiveBoardRoomResponseDto.of(room, count);
                        })
                .toList();
    }

    // 라이브 보드룸 삭제
    public String deleteRoom(String roomId) {
        LiveBoardRoom room = roomRepository.findRoomById(roomId);

        if (room == null) {
            return "ALREADY_DELETED";
        }
        roomRepository.deleteRoom(roomId);
        return "DELETED";
    }
}
