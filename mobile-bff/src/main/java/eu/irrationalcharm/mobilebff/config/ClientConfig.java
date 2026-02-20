package eu.irrationalcharm.mobilebff.config;

import eu.irrationalcharm.mobilebff.client.PersistenceServiceClient;
import eu.irrationalcharm.mobilebff.client.UserServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Slf4j
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

                if (!tokenValue.isEmpty())
                    log.debug("Injected JWT into downstream request");
            }

            return execution.execute(request, body);
        };

        var requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofMillis(2000));

        return builder
                .requestFactory(requestFactory)
                .requestInterceptor(interceptor)
                .build();
    }


    //Allows environment variables for HttpExchange
    @Bean
    public HttpServiceProxyFactory httpServiceProxyFactory(RestClient restClient, ConfigurableEnvironment env) {
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                .embeddedValueResolver(env::resolvePlaceholders)
                .build();
    }

    @Bean
    public UserServiceClient userServiceClient(HttpServiceProxyFactory factory) {
        return factory.createClient(UserServiceClient.class);
    }

    @Bean
    public PersistenceServiceClient persistenceServiceClient(HttpServiceProxyFactory factory) {
        return factory.createClient(PersistenceServiceClient.class);
    }

}
