package com.sparta.spartatigers.domain.liveboard.pubsub;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class LiveBoardMatchSubscriberConfig {
    private final LiveBoardMatchSubscriber liveBoardMatchSubscriber;
    private final RedisConnectionFactory connectionFactory;

    @Bean
    public RedisMessageListenerContainer redisLiveBoardMatchMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(
                liveBoardMatchSubscriber, new PatternTopic("live_board:game:*"));
        return container;
    }
}
