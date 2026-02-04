package eu.irrationalcharm.mobilebff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${jwkSetUri}")
    private String jwkSetUri;

    @Bean
    public SecurityFilterChain config(HttpSecurity http) {
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(
                jwt -> jwt.jwkSetUri(jwkSetUri)
        ));

        http.authorizeHttpRequests(request -> request
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated());
        http.csrf(CsrfConfigurer::disable);


        return http.build();
    }

    // This decorator captures the SecurityContext from the parent thread and wraps the runnable to re-apply it in the child thread.
    // Essentially we have SecurityContext in new threads or VT.
    @Bean
    public TaskDecorator securityContextTaskDecorator() {
        return DelegatingSecurityContextRunnable::new;
    }
}
