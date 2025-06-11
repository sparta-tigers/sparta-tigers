package com.sparta.spartatigers.domain.liveboard.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.liveboard.dto.response.LiveBoardRoomResponseDto;
import com.sparta.spartatigers.domain.liveboard.model.LiveBoardRoom;
import com.sparta.spartatigers.domain.liveboard.repository.LiveBoardRoomRepository;
import com.sparta.spartatigers.domain.match.model.entity.Match;
import com.sparta.spartatigers.domain.match.repository.MatchRepository;

@Service
@RequiredArgsConstructor
public class LiveBoardRoomService {

    private final LiveBoardRoomRepository roomRepository;
    private final MatchRepository matchRepository; // TODO: 경기일정 크롤러 확인하기 + 스케줄러

    // 라이브 보드룸 생성
    public List<LiveBoardRoomResponseDto> createTodayRoom() {

		// 오늘 경기 일정 찾기
		LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Match> matches = matchRepository.findAllByMatchTimeBetween(start, end);
		List<LiveBoardRoomResponseDto> todayRoomList = new ArrayList<>();

		// 라이브 보드룸 생성 후 저장
        for (Match match : matches) {
            String roomId = "ROOM_" + match.getId();
            String title = match.getAwayTeam().getName() + "VS" + match.getHomeTeam().getName();

            // 채팅방 운영시간 TODO: 끝나는시간?
            LocalDateTime roomStart = match.getMatchTime().minusMinutes(30);
            LocalDateTime roomClose =
                    match.getMatchTime().plusHours(4).plusMinutes(30);

            LiveBoardRoom room =
                    LiveBoardRoom.builder()
                            .roomId(roomId)
                            .matchId(match.getId())
                            .title(title)
                            .openAt(roomStart)
                            .closedAt(roomClose)
                            .connectCount(0)
                            .build();

            roomRepository.saveRoom(room);
			todayRoomList.add(LiveBoardRoomResponseDto.of(room));
        }
		return todayRoomList;
    }

	// 라이브 보드룸 전체 조회
	public List<LiveBoardRoomResponseDto> findAllRooms() {
		return roomRepository.findAllRoom().stream()
			.map(LiveBoardRoomResponseDto::of)
			.collect(Collectors.toList());
	}

	// 오늘의 라이브 보드룸 조회
	public List<LiveBoardRoomResponseDto> findTodayRooms() {
		LocalDateTime start = LocalDate.now().atStartOfDay();
		LocalDateTime end = start.plusDays(1);

		return roomRepository.findAllRoom().stream()
		.filter(room -> !room.getOpenAt().isBefore(start) && room.getOpenAt().isBefore(end))
		.map(LiveBoardRoomResponseDto::of)
		.toList();
	}

	// 라이브 보드룸 삭제
	public void deleteRoom(String roomId) {
		roomRepository.deleteRoom(roomId);
	}


	// 라이브 보드룸 접속자 수 증가
    public void increaseConnectCount(String roomId) {
        LiveBoardRoom room = roomRepository.findRoomById(roomId);
        room.increaseCount();
        roomRepository.saveRoom(room);
    }

	// 라이브 보드룸 접속자 수 감소
    public void decreaseConnectCount(String roomId) {
        LiveBoardRoom room = roomRepository.findRoomById(roomId);
        room.decreaseCount();
        roomRepository.saveRoom(room);
    }
}
