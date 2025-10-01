package eu.irrationalcharm.messaging_service.listener;

import eu.irrationalcharm.messaging_service.config.websocket.WebSocketSessionRegistry;
import eu.irrationalcharm.messaging_service.service.InternalUserService;
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
    private final InternalUserService internalUserService;
    private final WebSocketSessionRegistry sessionRegistry;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Authentication authentication = (Authentication) accessor.getUser();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if(authentication != null) {
            String idpUuid = authentication.getName();
            String sessionId = accessor.getSessionId();

            var userSocialGraph = internalUserService.getUserSocialGraphDto(idpUuid);

            userPresenceService.setUserOnline(userSocialGraph.username(), sessionId);
            sessionRegistry.registerSession(userSocialGraph.username(), sessionId);

            log.info("User connected: {}, session id: {}", idpUuid, sessionId);

        } else
            throw new RuntimeException("Something went wrong, user should be authenticated here!!");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Authentication authentication = (Authentication) accessor.getUser();

        if (authentication != null) {
            var userSocialGraph = internalUserService.getUserSocialGraphDto(authentication.getName());
            userPresenceService.setUserOffline(userSocialGraph.username());
            sessionRegistry.removeSession(userSocialGraph.username());

            log.info("User disconnected: {}, session id: {}", authentication.getName(), accessor.getSessionId());
        } else {
            log.info("Unauthenticated user disconnected, session id: {}", accessor.getSessionId());
        }
    }

}
