package eu.irrationalcharm.messaging_service.client.interceptor;

import eu.irrationalcharm.messaging_service.security.CustomWebSocketAuthToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String tokenValue = null;

        if (authentication instanceof JwtAuthenticationToken authenticationToken) {
            tokenValue = authenticationToken.getToken().getTokenValue();
        }

        if (authentication instanceof CustomWebSocketAuthToken customWebSocketAuthToken) {
            tokenValue = customWebSocketAuthToken.getJwt().getTokenValue();
        }


        if (tokenValue != null) {
            requestTemplate.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, tokenValue));
        }
    }
}
