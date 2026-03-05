package eu.irrationalcharm.userservice.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwkSetUri}")
    private String jwkSetUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt( jwtConfigurer -> jwtConfigurer.jwkSetUri(jwkSetUri)));

        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/api/v1/users/me").authenticated()
                .requestMatchers("/api/v1/users/{username}").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated());

        http.csrf(CsrfConfigurer::disable);

        return http.build();
    }
}
