package com.sparta.spartatigers.domain.liveboard.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.liveboard.service.LiveBoardService;
import com.sparta.spartatigers.domain.match.model.entity.Match;
import com.sparta.spartatigers.domain.match.repository.MatchRepository;
import com.sparta.spartatigers.domain.team.model.entity.Team;
import com.sparta.spartatigers.domain.team.repository.TeamRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class LiveBoardScheduler {

    // 경기 정보를 가져오기 위한 Repository
    private final MatchRepository matchRepository;
    // 채팅방 생성 로직을 담당하는 서비스
    private final LiveBoardService liveBoardService;

    private final TeamRepository teamRepository;

    // ✅ 서버 실행 시 1회 실행 (테스트용)
    @PostConstruct
    public void initTestRoom() {
        log.info("🔧 서버 시작 시 더미 채팅방 생성 테스트 실행");
        createTodayRooms(); // 기존 스케줄러 로직 재사용
    }

    @Scheduled(cron = "0 0 0/1 * * ?") // 1시간 단위
    public void createTodayRooms() {
        // 오늘 날짜 기준 시작 시각 (00:00:00)
        LocalDateTime start = LocalDate.now().atStartOfDay();
        // 오늘 날짜 기준 끝 시각 (내일 00:00:00)
        LocalDateTime end = start.plusDays(1);

        // 🔽 테스트용 더미 경기 데이터 --------
        // ✅ 1. Team 저장
        Team homeTeam = teamRepository.save(Team.builder().name("한화").build());
        Team awayTeam = teamRepository.save(Team.builder().name("기아").build());

        // ✅ 2. 저장된 Team으로 Match 생성
        Match dummyMatch =
                Match.builder()
                        .matchTime(LocalDateTime.now().plusHours(1))
                        .homeTeam(homeTeam) // ✔️ 여기!
                        .awayTeam(awayTeam) // ✔️ 여기!
                        .stadium(null)
                        .build();

        // ✅ 3. Match 저장
        Match savedMatch = matchRepository.save(dummyMatch);

        // ✅ 4. Redis용 채팅방 생성
        List<Match> todayMatches = List.of(savedMatch); // ✔️ savedMatch 써야 roomId 가능
        liveBoardService.createTodayRoom(todayMatches);
        // -----------------

        // 서비스에 주입
        // liveBoardService.createTodayRoom(todayMatches);

        // // 오늘 하루 예정된 모든 경기 조회
        // List<Match> todayMatches = matchRepository.findAllByMatchTimeBetween(start, end);
        //
        // // 각 경기마다 채팅방 생성 (Redis 저장 + 구독 설정)
        // liveBoardService.createTodayRoom(todayMatches);

        // 로그 출력 (생성된 채팅방 개수)
        log.info("[✅] {}개 채팅방이 자동 생성되었습니다.", todayMatches.size());
    }
}
