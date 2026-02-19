package eu.irrationalcharm.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimAccessor;

@Configuration
public class RateLimitingConfig {

    @Bean
    public KeyResolver jwtUserKeyResolver() {
        return _ -> ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getPrincipal())
                .cast(Jwt.class)
                .map(JwtClaimAccessor::getId) // Your Keycloak attribute
                .defaultIfEmpty("anonymous");
    }
}
