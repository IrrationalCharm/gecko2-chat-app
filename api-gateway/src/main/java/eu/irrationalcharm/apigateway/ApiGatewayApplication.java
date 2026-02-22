package eu.irrationalcharm.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class ApiGatewayApplication {

    static void main(String[] args) {
        //Tells WebFlux to copy MDC (trace_id) across threads
        Hooks.enableAutomaticContextPropagation();
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
