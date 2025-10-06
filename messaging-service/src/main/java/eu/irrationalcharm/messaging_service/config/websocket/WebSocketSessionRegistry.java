package eu.irrationalcharm.messaging_service.config.websocket;


import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionRegistry {

    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> subscribedSessionMap = new ConcurrentHashMap<>();


    /**
     * Removes both the user Session and subscribed sessions
     */
    public void userDisconnected(String username, String sessionId) {
        removeSession(sessionId);
        removeAllSubscribedSessions(username);
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
        return subscribedSessionMap.getOrDefault(username, ConcurrentHashMap.newKeySet())
                .stream().anyMatch(destination::equals);

    }

    public void addSubscribedSession(String username, String destination) {
        subscribedSessionMap.computeIfAbsent(username, k -> ConcurrentHashMap.newKeySet())
                .add(destination);
    }


    public void removeSubscribedSession(String username, String destination) {
        subscribedSessionMap.computeIfPresent(username, (key, destinations) -> {
            destinations.remove(destination);
            return destinations.isEmpty() ? null : destinations;
        });
    }


    public void removeAllSubscribedSessions(String username) {
        subscribedSessionMap.remove(username);
    }
}
