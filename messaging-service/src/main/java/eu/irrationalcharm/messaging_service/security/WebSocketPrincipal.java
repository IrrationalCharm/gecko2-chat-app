package eu.irrationalcharm.messaging_service.security;

import lombok.Getter;

import java.security.Principal;

public class WebSocketPrincipal implements Principal {

    private final String username;
    @Getter
    private final String providerId;

    public WebSocketPrincipal(String username, String providerId) {
        this.username = username;
        this.providerId = providerId;
    }

    @Override
    public String getName() {
        return username;
    }
}
