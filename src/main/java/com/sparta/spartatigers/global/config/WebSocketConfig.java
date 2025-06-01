package com.sparta.spartatigers.global.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.global.handler.DefaultWebSocketHandshakeHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * WebSocket Handler를 등록하기 위한 설정 클래스 EnableWebSocket -> WebSocket 사용하도록 지원
 * EnalbeWebSocketMessageBorker -> STOMP 사용하도록
 */
@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * /ws로 연결 요청을 보내도록 설정 javaScipt ex) const socket = new SockJS('/ws'); withSockJS WebSocket을
     * 지원하지 않는 브라우저에서도 대체 전송 프로토콜을 사용하도록 (폴링)
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setHandshakeHandler(new DefaultWebSocketHandshakeHandler())
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * STOMP 기반 메세지 브로커 설정 /topic으로 시작하는 메시지 주소를 브로드 캐스트 ex) 클라이언트가 /topic/chatroom을 구독하면 서버에서 해당
     * 경로로 메세지를 발행하면 모두 수신 /app 서버가 클라이언트에서 수신하는 STOMP 메시지의 시작 경로 ex) 클라이언트가 /app/message 경로로 메세지를
     * 발송하면 서버는 이를 처리함
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // @MessageMapping(liveboard.)
        // @MessageMapping("/dm.")
        // js ex) stompClinet.subscribe('/topic/chat/')
        registry.enableSimpleBroker("/server");
        // js ex) stompClient.send("/app/chat.send")
        registry.setApplicationDestinationPrefixes("/client");
    }

    // 추후 참고할 만한 코드: https://modutaxi-tech.tistory.com/6

    private final ObjectMapper objectMapper;

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

        converter.setObjectMapper(objectMapper); // 👈 ObjectMapper 주입
        converter.setSerializedPayloadClass(String.class); // 👈 payload는 String으로 받는다 선언

        messageConverters.add(converter);
        return false; // 기본 컨버터는 사용하지 않음
    }
}
