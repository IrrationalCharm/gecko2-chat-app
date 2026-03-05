package eu.irrationalcharm.messaging_service.security;


import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import static eu.irrationalcharm.messaging_service.security.JwtClaims.*;


@Component
public class JwtAuthenticationConverter implements Converter<Jwt, CustomWebSocketAuthToken> {
    @Override
    public CustomWebSocketAuthToken convert(Jwt source) {
        String internalId = source.getClaimAsString(INTERNAL_ID);
        String providerId = source.getClaimAsString(SUBJECT);
        String username = source.getClaimAsString(USERNAME_APP);
        String email = source.getClaimAsString(EMAIL);

        if (internalId == null || providerId == null || username == null || email == null)
            throw new BadJwtException("Invalid JWT, Missing claims in Jwt");

        WebSocketPrincipal webSocketPrincipal = new WebSocketPrincipal(internalId, providerId);

        return new CustomWebSocketAuthToken(webSocketPrincipal, username, email, source);
    }
}
