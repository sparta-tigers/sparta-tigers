package com.sparta.spartatigers.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.sparta.spartatigers.domain.chatroom.pubsub.RedisDirectMessageSubscriber;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisDirectRoomMessageSubscriberConfig {

    private final RedisConnectionFactory connectionFactory;
    private final RedisDirectMessageSubscriber subscriber;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // 모든 directRoom:{id} 형식의 채널 구독
        container.addMessageListener(subscriber, new PatternTopic("directRoom:*"));
        log.info("연결된 채팅방: directRoom:*");

        return container;
    }
}
