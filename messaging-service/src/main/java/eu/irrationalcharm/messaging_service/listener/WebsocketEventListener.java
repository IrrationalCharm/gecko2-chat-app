package eu.irrationalcharm.messaging_service.listener;

import eu.irrationalcharm.messaging_service.client.UserServiceClient;
import eu.irrationalcharm.messaging_service.client.dto.UserSocialGraphDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebsocketEventListener {

    private final UserServiceClient userServiceClient;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {





        System.out.println("connected!!!");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        System.out.println("disconnected :(");
    }

}
