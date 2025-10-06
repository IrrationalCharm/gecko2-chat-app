package eu.irrationalcharm.messaging_service.listener;

import eu.irrationalcharm.messaging_service.config.websocket.WebSocketSessionRegistry;
import eu.irrationalcharm.messaging_service.security.CustomWebSocketAuthToken;
import eu.irrationalcharm.messaging_service.security.WebSocketPrincipal;
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
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketEventListener {

    private final UserPresenceService userPresenceService;
    private final WebSocketSessionRegistry sessionRegistry;


    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Authentication authentication = (Authentication) accessor.getUser();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if(authentication != null) {
            WebSocketPrincipal principal = (WebSocketPrincipal) authentication.getPrincipal();
            String sessionId = accessor.getSessionId();

            userPresenceService.refreshUserOnline(principal.getName(), sessionId);
            sessionRegistry.registerSession(principal.getName(), sessionId);

            log.info("User connected: {}, session id: {}", principal.getName(), sessionId);

        } else
            throw new RuntimeException("Something went wrong, user should be authenticated here!!");
    }


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        CustomWebSocketAuthToken authentication = (CustomWebSocketAuthToken) accessor.getUser();

        if (authentication != null) {
            userPresenceService.setUserOffline(authentication.getName());
            sessionRegistry.userDisconnected(authentication.getName(), accessor.getSessionId());

            log.info("User disconnected: {}, session id: {}", authentication.getName(), accessor.getSessionId());
        } else {
            log.info("Unauthenticated user disconnected, session id: {}", accessor.getSessionId());
        }
    }


    @EventListener
    public void handleWebSocketSubscribedListener(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        CustomWebSocketAuthToken authentication = (CustomWebSocketAuthToken) accessor.getUser();

        if (authentication != null) {
            sessionRegistry.addSubscribedSession(authentication.getName(), accessor.getDestination());

            log.info("User subscribed: {}, to destination: {}", authentication.getName(), accessor.getDestination());
        } else {
            log.warn("Unauthenticated user subscribed, this should not occur! {}", accessor.getSessionId());
        }
    }


    @EventListener
    public void handleWebSocketUnSubscribedListener(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        CustomWebSocketAuthToken authentication = (CustomWebSocketAuthToken) accessor.getUser();

        if (authentication != null) {
            sessionRegistry.removeSubscribedSession(authentication.getName(), accessor.getDestination());

            log.info("User unsubscribed: {}, to destination: {}", authentication.getName(), accessor.getDestination());
        } else {
            log.warn("Unauthenticated user subscribed, this should not occur! {}", accessor.getSessionId());
        }
    }
}
