package eu.irrationalcharm.messaging_service.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;


public class CustomWebSocketAuthToken extends AbstractAuthenticationToken {

    private final WebSocketPrincipal webSocketPrincipal;
    @Getter
    private final Jwt jwt;

    public CustomWebSocketAuthToken(WebSocketPrincipal webSocketPrincipal, Jwt jwt) {
        super(null); //TODO maybe implement authorities eventually
        this.webSocketPrincipal = webSocketPrincipal;
        this.jwt = jwt;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return webSocketPrincipal;
    }
}
