package eu.irrationalcharm.mobilebff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        // Define the interceptor to run PER REQUEST
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof JwtAuthenticationToken jwtToken) {
                String tokenValue = jwtToken.getToken().getTokenValue();
                request.getHeaders().setBearerAuth(tokenValue);
            }

            return execution.execute(request, body);
        };

        return builder
                .requestInterceptor(interceptor)
                .build();
    }

    /**
    @Bean
    HttpServiceProxyFactory proxyFactory(RestClient restClient) {
        return HttpServiceProxyFactory.builder()
                .exchangeAdapter(RestClientAdapter.create(restClient))
                .build();
    }


    //Clients

    @Bean
    UserService userService(HttpServiceProxyFactory proxyFactory) {
        return proxyFactory.createClient(UserService.class);
    }

    @Bean
    PersistenceService persistenceService(HttpServiceProxyFactory proxyFactory) {
        return proxyFactory.createClient(PersistenceService.class);
    }
    **/
}
