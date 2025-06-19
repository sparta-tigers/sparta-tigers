package com.sparta.spartatigers.domain.chatroom.listener;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.chatroom.repository.DirectRoomRepository;
import com.sparta.spartatigers.global.exception.ExceptionCode;
import com.sparta.spartatigers.global.exception.InvalidRequestException;

@Component
@RequiredArgsConstructor
@Slf4j
public class DirectRoomSubscribeValidator {

    private final DirectRoomRepository directRoomRepository;

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String destination = accessor.getDestination();
        if (destination == null || !destination.startsWith("/server/directRoom/")) {
            return;
        }

        try {
            Long roomId = extractRoomId(destination);
            if (!directRoomRepository.existsById(roomId)) {
                log.warn("존재하지 않는 채팅방 구독 시도: {}", roomId);
                throw new InvalidRequestException(ExceptionCode.CHATROOM_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            log.warn("잘못된 채팅방 구독 시도: {}", destination);
            throw new InvalidRequestException(ExceptionCode.CHATROOM_NOT_FOUND);
        }
    }

    private Long extractRoomId(String destination) {
        return Long.parseLong(destination.substring("/server/directRoom/".length()));
    }
}
