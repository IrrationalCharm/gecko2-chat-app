package eu.irrationalcharm.userservice.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${jwkSetUri}")
    private String jwkSetUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt( jwtConfigurer -> jwtConfigurer.jwkSetUri(jwkSetUri)));

        http.authorizeHttpRequests(requests -> requests.anyRequest().authenticated());

        http.csrf(CsrfConfigurer::disable);

        return http.build();
    }
}
