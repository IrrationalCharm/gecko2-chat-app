package eu.irrationalcharm.userservice.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class Config {

    /**
     * OAuth2ClientHttpRequestInterceptor intercepts each request and adds the token, which is fetched and cached automatically.
     */
    @Bean
    public RestClient restClient(RestClient.Builder builder,
                                 OAuth2AuthorizedClientManager authorizedClientManager) {
        var requestInterceptor = new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        builder.requestInterceptor(requestInterceptor);

        return builder.build();
    }
}
