package eu.irrationalcharm.messaging_service.config.security;

import eu.irrationalcharm.messaging_service.security.JwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("{$jwkSetUri}")
    private String jwkUri;

    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> {
                    jwtConfigurer.jwkSetUri(jwkUri);
                    jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter);
                }));

        httpSecurity.authorizeHttpRequests(request -> request
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .anyRequest().authenticated()
        );


        httpSecurity.csrf(CsrfConfigurer::disable);
        return httpSecurity.build();
    }

}
