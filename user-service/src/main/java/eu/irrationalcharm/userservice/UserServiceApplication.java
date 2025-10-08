package eu.irrationalcharm.userservice;

import eu.irrationalcharm.userservice.properties.KeycloakAdminProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableKafka
@SpringBootApplication
@EnableWebSecurity
@AllArgsConstructor
@EnableJpaRepositories
@EnableJpaAuditing
@EnableConfigurationProperties(value = {KeycloakAdminProperties.class})
public class UserServiceApplication  {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }


}
