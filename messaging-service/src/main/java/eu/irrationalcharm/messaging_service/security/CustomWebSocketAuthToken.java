package eu.irrationalcharm.messaging_service.security;

import lombok.Getter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

import java.util.Map;


/**
 * Personalized Authentication so that we can have our own implementation of Principal WebSocketPrincipal, where we associate
 * the providerId given by the Authorization server and username provided by user-service.
 */
public class CustomWebSocketAuthToken extends AbstractOAuth2TokenAuthenticationToken<Jwt> {

    private final WebSocketPrincipal webSocketPrincipal;
    @Getter
    private final String username;

    @Getter
    private final String email;

    public CustomWebSocketAuthToken(WebSocketPrincipal webSocketPrincipal, String username, String email, Jwt jwt) {
        super(jwt); //TODO maybe implement authorities eventually
        this.webSocketPrincipal = webSocketPrincipal;
        this.username = username;
        this.email = email;
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

    public String getProviderId() {
        return webSocketPrincipal.getProviderId();
    }
}
