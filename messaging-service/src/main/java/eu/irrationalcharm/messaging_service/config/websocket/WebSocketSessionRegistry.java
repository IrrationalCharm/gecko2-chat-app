package eu.irrationalcharm.messaging_service.config.websocket;


import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionRegistry {

    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();

    public void registerSession(String username, String sessionId) {
        userSessionMap.put(username, sessionId);
    }

    public void removeSession(String sessionId) {
        userSessionMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(sessionId))
                .findFirst()
                .ifPresent(entry -> userSessionMap.remove(entry.getKey()));
    }

    public Optional<String> getSession(String username) {
        return Optional.ofNullable(userSessionMap.get(username));
    }
}
