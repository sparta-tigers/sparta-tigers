package com.sparta.spartatigers.domain.liveboard.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sparta.spartatigers.domain.liveboard.model.LiveBoardRoom;
import com.sparta.spartatigers.domain.liveboard.repository.LiveBoardRoomRepository;
import com.sparta.spartatigers.domain.match.model.entity.Match;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LiveBoardRoomService {

	private final LiveBoardRoomRepository roomRepository;
	private final MatchRepository matchRepository; // TODO: 경기일정 크롤러 확인하기

	public void createTodayRooms() {
		LocalDateTime start = LocalDate.now().atStartOfDay();
		LocalDateTime end = start.plusDays(1);

		List<Match> matches = matchRepository.findAllByMatchTimeBetween(start, end);

		for (Match match : matches.stream().toList()) {
			String roomId = "ROOM_"+ match.getId();
			String title = match.getAwayTeam().getName() + "VS" + match.getHomeTeam().getName();

			// 채팅방 운영시간 TODO: 끝나는시간?
			LocalDateTime roomStart = match.getMatchTime().minusMinutes(30);
			LocalDateTime roomClose = match.getMatchTime().plusHours(4).plusMinutes(30); // 경기 4시간으로 일단 잡아둠..

			LiveBoardRoom room = new LiveBoardRoom(
				roomId,
				match,
				title,
				roomStart,
				roomClose
			);

			roomRepository.save(room);
		}
	}
}


