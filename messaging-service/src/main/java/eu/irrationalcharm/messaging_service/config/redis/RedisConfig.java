package eu.irrationalcharm.messaging_service.config.redis;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.irrationalcharm.messaging_service.client.dto.UserSocialGraphDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        var objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Use Jackson2JsonRedisSerializer with target class
        var valueSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, UserSocialGraphDto.class);


        var redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .entryTtl(Duration.ofDays(1));

        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }


    /**
     * Creates a new channel in the redis pub/sub if the channel already exists, it subscribes to it and listens to it.
     * @param connectionFactory helps us connect to the redis pub/sub
     * @param subscriber the channel to whom we will join or create.
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
                                                                       RedisMessageReceiver subscriber) {
        var container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // new PatternTopic("user:*") Subscribes to all channels that match a pattern. This is useful if you wanted to route messages for specific users to specific channels
        container.addMessageListener(subscriber, new ChannelTopic("queue/private/messages"));

        return container;
    }


    /**
     * StringRedisTemplate is a string based general purpose for working with Redis.
     * We can use it for Pub/Sub and caching for example
     * @param redisConnectionFactory helps us connect to the redis pub/sub
     * @return StringRedisTemplate
     */
    @Bean
    StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        var template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);

        return template;
    }
}
