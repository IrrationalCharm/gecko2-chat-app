package eu.irrationalcharm.messaging_service.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
public class WebSocketSecurityConfig {

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                .nullDestMatcher().permitAll()
                .simpTypeMatchers(SimpMessageType.DISCONNECT).permitAll()
                .simpTypeMatchers(SimpMessageType.CONNECT).authenticated()
                .simpDestMatchers("/app/**").authenticated()
                .anyMessage().denyAll();

        return messages.build();
    }
}
