package eu.irrationalcharm.messaging_service.client.interceptor;

import eu.irrationalcharm.messaging_service.security.CustomWebSocketAuthToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        CustomWebSocketAuthToken authToken = (CustomWebSocketAuthToken) SecurityContextHolder.getContext().getAuthentication();

        String tokenValue = authToken.getJwt().getTokenValue();

        requestTemplate.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, tokenValue));
    }
}
