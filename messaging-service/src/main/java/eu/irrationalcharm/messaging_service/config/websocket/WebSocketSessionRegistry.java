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
    public void userDisconnected(String userId, String sessionId) {
        removeSession(sessionId);
        removeAllSubscribedSessions(userId);
    }


    public void registerSession(String userId, String sessionId) {
        userSessionMap.put(userId, sessionId);
    }

    public void removeSession(String sessionId) {
        userSessionMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(sessionId))
                .findFirst()
                .ifPresent(entry -> userSessionMap.remove(entry.getKey()));
    }


    //Returns session id if found, otherwise returns empty Optional
    public Optional<String> getSession(String userId) {
        return Optional.ofNullable(userSessionMap.get(userId));
    }


    //Find out if the user is connected to this websocket
    public boolean isRegistered(String userId) {
        return userSessionMap.get(userId) != null;
    }


    public boolean isSubscribed(String userId, String destination) {
        return subscribedSessionMap.getOrDefault(userId, ConcurrentHashMap.newKeySet())
                .stream().anyMatch(destination::equals);

    }

    public void addSubscribedSession(String userId, String destination) {
        subscribedSessionMap.computeIfAbsent(userId, _ -> ConcurrentHashMap.newKeySet())
                .add(destination);
    }


    public void removeSubscribedSession(String userId, String destination) {
        subscribedSessionMap.computeIfPresent(userId, (_, destinations) -> {
            destinations.remove(destination);
            return destinations.isEmpty() ? null : destinations;
        });
    }


    public void removeAllSubscribedSessions(String userId) {
        subscribedSessionMap.remove(userId);
    }
}
