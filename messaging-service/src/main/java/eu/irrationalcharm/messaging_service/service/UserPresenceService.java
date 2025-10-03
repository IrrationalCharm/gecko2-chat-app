package eu.irrationalcharm.messaging_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserPresenceService {

    private static final String USER_STATUS_KEY_PREFIX = "user:status:";
    private static final String ONLINE_USERS_KEY = "online:users";
    private static final long USER_STATUS_TTL_MINUTES = 5;

    private final RedisTemplate<String, String> redisTemplate;


    public void setUserOnline(String username, String sessionId) {
        String key = USER_STATUS_KEY_PREFIX + username;

        Map<String, String> statusDetails = Map.of(
                "status", "online",
                "lastSeen", String.valueOf(System.currentTimeMillis()),
                "websocketSessionId", sessionId
        );

        redisTemplate.opsForHash().putAll(key, statusDetails);
        redisTemplate.expire(key, Duration.ofMinutes(USER_STATUS_TTL_MINUTES));

        redisTemplate.opsForSet().add(ONLINE_USERS_KEY, username);
        redisTemplate.expire(ONLINE_USERS_KEY + username, Duration.ofMinutes(USER_STATUS_TTL_MINUTES));
    }

    public boolean isUserOnline(String username) {
        Boolean isOnline = redisTemplate.opsForSet().isMember(ONLINE_USERS_KEY, username);
        if (isOnline == null)
            return false;

        return isOnline;
    }

    public void setUserOffline(String username) {
        String key = USER_STATUS_KEY_PREFIX + username;

        redisTemplate.delete(key);
        redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, username);
    }



}
