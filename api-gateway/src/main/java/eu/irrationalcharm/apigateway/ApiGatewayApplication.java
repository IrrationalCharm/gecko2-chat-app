package eu.irrationalcharm.apigateway;

import io.micrometer.observation.ObservationPredicate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class ApiGatewayApplication {

    static void main(String[] args) {
        //Tells WebFlux to copy MDC (trace_id) across threads
        Hooks.enableAutomaticContextPropagation();
        SpringApplication.run(ApiGatewayApplication.class, args);

        System.out.println("Version 3!!!");
    }

    @Bean
    public ObservationPredicate disableSecurityObservation() {
        return (name, context) -> !name.startsWith("spring.security.");
    }
}
