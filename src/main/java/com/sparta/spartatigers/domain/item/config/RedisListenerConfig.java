package com.sparta.spartatigers.domain.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import lombok.RequiredArgsConstructor;

import com.sparta.spartatigers.domain.item.pubsub.LocationSubscriber;

@Configuration
@RequiredArgsConstructor
public class RedisListenerConfig {

    private final LocationSubscriber locationSubscriber;
    private final RedisConnectionFactory connectionFactory;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(locationSubscriber, new ChannelTopic("location-channel"));
        return container;
    }
}
