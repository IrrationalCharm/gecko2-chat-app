package eu.irrationalcharm.apigateway.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${jwkSetUri}")
    private String jwkSetUri;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain (ServerHttpSecurity httpSecurity) {

        httpSecurity.oauth2ResourceServer(oAuth2 ->
                oAuth2.jwt(jwtSpec -> jwtSpec.jwkSetUri(jwkSetUri)));

        httpSecurity.authorizeExchange(authorize -> { authorize
                .pathMatchers("/ws/**").permitAll() //TODO can maybe be changed to verify token before websocket upgrade
                .anyExchange().authenticated();
        });

        httpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable);
        return httpSecurity.build();
    }
}
