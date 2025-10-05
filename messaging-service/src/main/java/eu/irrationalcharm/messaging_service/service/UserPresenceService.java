package eu.irrationalcharm.messaging_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserPresenceService {

    private static final String USER_STATUS_KEY_PREFIX = "user:status:";
    //private static final long USER_STATUS_TTL_MINUTES = 3;

    private final RedisTemplate<String, String> redisTemplate;


    public void refreshUserOnline(String username, String sessionId) {
        String key = USER_STATUS_KEY_PREFIX + username;

        Map<String, String> statusDetails = Map.of(
                "status", "online",
                "lastSeen", String.valueOf(System.currentTimeMillis())
                //"websocketSessionId", sessionId
        );

        redisTemplate.opsForHash().putAll(key, statusDetails);
        //redisTemplate.expire(key, Duration.ofMinutes(USER_STATUS_TTL_MINUTES));

    }


    public boolean isUserOnline(String username) {
        String key = USER_STATUS_KEY_PREFIX + username;

        return redisTemplate.hasKey(key);
    }


    public void setUserOffline(String username) {
        String key = USER_STATUS_KEY_PREFIX + username;

        redisTemplate.delete(key);
    }


    public long lastSeen(String username) {
        String key = USER_STATUS_KEY_PREFIX + username;
        Object lastSeen = redisTemplate.opsForHash().get(key, "lastSeen");

        return lastSeen == null ? 0L : Long.parseLong((String) lastSeen);
    }
}
