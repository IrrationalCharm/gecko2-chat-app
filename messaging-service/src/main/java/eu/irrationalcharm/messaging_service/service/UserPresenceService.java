package eu.irrationalcharm.messaging_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserPresenceService {

    private static final String SESSIONS_KEY_PREFIX = "user:sessions:";
    private static final String LAST_SEEN_KEY_PREFIX = "user:lastseen:";
    private final RedisTemplate<String, String> redisTemplate;


    public void refreshUserOnline(String userId, String sessionId) {
        String sessionsKey = SESSIONS_KEY_PREFIX + userId;
        String lastSeenKey = LAST_SEEN_KEY_PREFIX + userId;

        // 1. Add this specific session to the set of active sessions
        redisTemplate.opsForSet().add(sessionsKey, sessionId);

        // 2. Update the global last seen timestamp for the user
        redisTemplate.opsForValue().set(lastSeenKey, String.valueOf(System.currentTimeMillis()));

        // Optional: Set a TTL on the set as a safety net (e.g., 24 hours)
        // redisTemplate.expire(sessionsKey, Duration.ofHours(24));
    }

    /**
     * Called on SessionDisconnectEvent
     */
    public void setUserOffline(String userId, String sessionId) {
        String sessionsKey = SESSIONS_KEY_PREFIX + userId;

        redisTemplate.opsForSet().remove(sessionsKey, sessionId);

    }

    /**
     * Check if the user is online on ANY device
     */
    public boolean isUserOnline(String userId) {
        String sessionsKey = SESSIONS_KEY_PREFIX + userId;
        Long size = redisTemplate.opsForSet().size(sessionsKey);
        return size != null && size > 0;
    }

    public long lastSeen(String userId) {
        String lastSeenKey = LAST_SEEN_KEY_PREFIX + userId;
        String val = redisTemplate.opsForValue().get(lastSeenKey);
        return val == null ? 0L : Long.parseLong(val);
    }
}
