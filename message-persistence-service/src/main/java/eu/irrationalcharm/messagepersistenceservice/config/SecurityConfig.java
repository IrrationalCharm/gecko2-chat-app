package eu.irrationalcharm.messagepersistenceservice.config;


import eu.irrationalcharm.messagepersistenceservice.security.JwtAuthenticationConverter;
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


    @Value("${jwkSetUri}")
    private String jwkSetUri;

    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Bean
    public SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer(oauth2Configurer ->
                oauth2Configurer.jwt(jwtConfigurer ->  {
                    jwtConfigurer.jwkSetUri(jwkSetUri);
                    jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter);
                }));

        http.authorizeHttpRequests(httpRequest -> httpRequest.anyRequest().authenticated());

        http.csrf(CsrfConfigurer::disable);


        return http.build();
    }

}
