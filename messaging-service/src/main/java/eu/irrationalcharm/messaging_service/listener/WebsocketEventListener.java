package eu.irrationalcharm.messaging_service.listener;

import eu.irrationalcharm.messaging_service.service.UserPresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketEventListener {

    private final UserPresenceService userPresenceService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Authentication authentication = (Authentication) accessor.getUser();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if(authentication != null) {
            String userId = authentication.getName();
            String sessionId = accessor.getSessionId();
            userPresenceService.setUserOnline(authentication.getName(), accessor.getSessionId());

            log.info("User connected: {}, session id: {}", userId, sessionId);

        } else throw new RuntimeException("Something went wrong, user should be authenticated here!!");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Authentication authentication = (Authentication) accessor.getUser();

        if (authentication != null) {
            userPresenceService.setUserOffline(authentication.getName());
            log.info("User disconnected: {}, session id: {}", authentication.getName(), accessor.getSessionId());
        } else {
            log.info("Unauthenticated user disconnected, session id: {}", accessor.getSessionId());
        }


    }

}
