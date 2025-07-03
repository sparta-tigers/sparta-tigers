package com.sparta.spartatigers.domain.alarm.controller;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
public class TestController {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping(path = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter testEmitter() {
        Long id = 15L;
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);
        emitters.put(id, emitter);

        emitter.onCompletion(() -> emitters.remove(id));
        emitter.onTimeout(() -> emitters.remove(id));

        try {
            emitter.send(SseEmitter.event().name("").data(""));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

    @GetMapping("/send")
    public String sendTestAlarm() {
        Long userId = 15L; // 구독한 사용자 id (테스트용)
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name("alarm") // 이벤트 이름은 'alarm'으로
                                .data("테스트알람")); // 보낼 데이터 (문자열 또는 객체)
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(userId); // 에러 시 연결 해제
            }
        }
        return "알람 전송 완료";
    }
}
