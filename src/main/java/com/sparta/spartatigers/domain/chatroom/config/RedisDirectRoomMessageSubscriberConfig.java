package com.sparta.spartatigers.domain.chatroom.config;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
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

    @PostConstruct
    public void init() {
        log.info("RedisDirectRoomMessageSubscriberConfig initialized");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        log.info("Creating RedisMessageListenerContainer and adding listener...");
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        String topic = "directroom:1";
        log.info("👉 Adding message listener for topic: {}", topic);
        container.addMessageListener(subscriber, new ChannelTopic(topic));

        return container;
    }
}
