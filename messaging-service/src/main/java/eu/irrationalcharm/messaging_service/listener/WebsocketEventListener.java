package eu.irrationalcharm.messaging_service.listener;

import eu.irrationalcharm.messaging_service.client.UserServiceClient;
import eu.irrationalcharm.messaging_service.client.dto.UserSocialGraphDto;
import eu.irrationalcharm.messaging_service.service.InternalUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebsocketEventListener {

    private final InternalUserService internalUserService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Authentication authentication = (Authentication) accessor.getUser();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if(authentication != null) {
            System.out.printf("connected and authenticated %s!!!%n",authentication.getName());

            UserSocialGraphDto userSocialGraphDto = internalUserService.getUserSocialGraphDto(authentication.getName());
            System.out.println(userSocialGraphDto);

        } else {
            System.out.println("User connected without authentication");
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        System.out.println("disconnected :(");
    }

}
