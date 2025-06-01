package com.sparta.spartatigers.domain.liveboard.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.liveboard.dto.response.LiveBoardRoomResponseDto;
import com.sparta.spartatigers.domain.liveboard.model.LiveBoardMessage;
import com.sparta.spartatigers.domain.liveboard.model.LiveBoardRoom;
import com.sparta.spartatigers.domain.liveboard.model.MessageType;
import com.sparta.spartatigers.domain.liveboard.pubsub.RedisMessageSubscriber;
import com.sparta.spartatigers.domain.liveboard.repository.LiveBoardRoomRepository;
import com.sparta.spartatigers.domain.match.model.entity.Match;

@Service
@RequiredArgsConstructor
public class LiveBoardService {

    private final LiveBoardRoomRepository roomRepository;
    private final RedisMessageSubscriber redisSubscriber;
    private final RedisMessageListenerContainer redisMessageListener;
    // private final MatchRepository matchRepository; // TODO: 경기일정 크롤러 확인하기 + 스케줄러

    private Map<String, ChannelTopic> topics = new ConcurrentHashMap<>(); // 채팅방별 topic을 roomId로 찾기

    // 채팅방 입장
    public void enterRoom(String roomId) {
        if (!topics.containsKey(roomId)) {
            ChannelTopic topic = new ChannelTopic(roomId);
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            topics.put(roomId, topic);
        }
    }

    // 채팅방 전체 조회
    public List<LiveBoardRoomResponseDto> findAllRoom() {
        return roomRepository.findAllRoom().stream().map(LiveBoardRoomResponseDto::of).toList();
    }

    // 채팅방 단건 조회
    public LiveBoardRoom findRoomById(String roomId) {
        return roomRepository.findRoomById(roomId);
    }

    // 채널 토픽 반환 (publish시 사용)
    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }

    // 채팅방 생성
    public void createTodayRoom(List<Match> matches) {
        for (Match match : matches) {
            String roomId = "ROOM_" + match.getId();
            String title = match.getAwayTeam().getName() + "VS" + match.getHomeTeam().getName();
            LocalDateTime roomOpen = match.getMatchTime().minusMinutes(30);
            LocalDateTime roomClose = match.getMatchTime().plusHours(5).plusMinutes(30);

            LiveBoardRoom room =
                    LiveBoardRoom.builder()
                            .roomId(roomId)
                            .matchId(match.getId())
                            .title(title)
                            .openAt(roomOpen)
                            .closedAt(roomClose)
                            .isClosed(false)
                            .connectCount(0)
                            .build();

            roomRepository.saveRoom(room);
        }
    }

    // 채팅방 접속자 수 증감 처리
    public void updateConnectCount(LiveBoardMessage message) {
        String roomId = message.getRoomId();
        LiveBoardRoom room = roomRepository.findRoomById(roomId);
        if (message.getType() == MessageType.ENTER) {
            room.increaseCount();
        } else if (message.getType() == MessageType.QUIT) {
            room.decreaseCount();
        }
        roomRepository.saveRoom(room);
    }
}
