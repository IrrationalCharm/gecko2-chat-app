package eu.irrationalcharm.messaging_service.security;

import lombok.Getter;

import java.security.Principal;

public class WebSocketPrincipal implements Principal {

    private final String internalId;
    @Getter
    private final String providerId;

    public WebSocketPrincipal(String internalId, String providerId) {
        this.internalId = internalId;
        this.providerId = providerId;
    }

    @Override
    public String getName() {
        return internalId;
    }
}
