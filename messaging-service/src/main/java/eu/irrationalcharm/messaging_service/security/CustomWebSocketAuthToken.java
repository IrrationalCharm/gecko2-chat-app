package eu.irrationalcharm.messaging_service.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

import java.util.Map;


/**
 * Personalized Authentication so that we can have our own implementation of Principal WebSocketPrincipal, where we associate
 * the providerId given by the Authorization server and username provided by user-service.
 */
public class CustomWebSocketAuthToken extends AbstractOAuth2TokenAuthenticationToken<Jwt> {

    private final WebSocketPrincipal webSocketPrincipal;

    public CustomWebSocketAuthToken(WebSocketPrincipal webSocketPrincipal, Jwt jwt) {
        super(jwt); //TODO maybe implement authorities eventually
        this.webSocketPrincipal = webSocketPrincipal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Map<String, Object> getTokenAttributes() {
        return this.getToken().getClaims();
    }

    @Override
    public Object getPrincipal() {
        return this.webSocketPrincipal;
    }
}
