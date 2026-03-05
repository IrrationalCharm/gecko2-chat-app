package eu.irrationalcharm.userservice.config.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "eu.irrationalcharm.userservice.repository")
@EnableJpaAuditing
public class JpaConfig {
}
