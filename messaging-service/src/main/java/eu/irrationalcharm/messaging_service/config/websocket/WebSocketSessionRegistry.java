package eu.irrationalcharm.messaging_service.config.websocket;


import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionRegistry {

    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();
    private final Map<String, String> subscribedSessionMap = new ConcurrentHashMap<>();


    /**
     * Removes both the user Session and subscribed sessions
     */
    public void userDisconnected(String username, String sessionId) {
        removeSession(sessionId);
        removeSubscribedSession(username);
    }


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


    public boolean isSubscribed(String username, String destination) {
        return subscribedSessionMap.entrySet().stream()
                .anyMatch(entry -> entry.getKey().equals(username) && entry.getValue().equals(destination));
    }

    public void addSubscribedSession(String username, String destination) {
        subscribedSessionMap.put(username, destination);
    }

    public void removeSubscribedSession(String username) {
        subscribedSessionMap.remove(username);
    }
}
