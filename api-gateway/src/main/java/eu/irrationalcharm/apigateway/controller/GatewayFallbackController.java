package eu.irrationalcharm.apigateway.controller;

import eu.irrationalcharm.apigateway.dto.ApiResponse;
import eu.irrationalcharm.apigateway.dto.ErrorResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class GatewayFallbackController {

    private static final Logger log = LoggerFactory.getLogger(GatewayFallbackController.class);

    @RequestMapping("/user-service") //RequestMapping allows us to cover all HTTP request types
    public Mono<ResponseEntity<ErrorResponseDto<Void>>> userServiceFallback(ServerWebExchange exchange) {
        logException(exchange, "User Service");
        return buildResponse("User Service");
    }

    @RequestMapping("/persistence")
    public Mono<ResponseEntity<ErrorResponseDto<Void>>> persistenceFallback(ServerWebExchange exchange) {
        logException(exchange, "Message Persistence Service");
        return buildResponse("Message Persistence Service");
    }

    @RequestMapping("/mobile-bff")
    public Mono<ResponseEntity<ErrorResponseDto<Void>>> bffFallback(ServerWebExchange exchange) {
        logException(exchange, "Mobile BFF");
        return buildResponse("Mobile BFF");
    }

    @RequestMapping("/media")
    public Mono<ResponseEntity<ErrorResponseDto<Void>>> mediaFallback(ServerWebExchange exchange) {
        logException(exchange, "Media Service");
        return buildResponse("Media Service");
    }

    // Helper method to build the 503 response
    private Mono<ResponseEntity<ErrorResponseDto<Void>>> buildResponse(String serviceName) {
        return Mono.just(
                ApiResponse.error(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                HttpStatus.SERVICE_UNAVAILABLE.toString(),
                                String.format("Service %s is not responding", serviceName),
                                null)
        );
    }

    private void logException(ServerWebExchange exchange, String serviceName) {
        Throwable exception = exchange.getAttribute(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);
        if (exception != null) {
            log.error("Fallback triggered for {} due to: {}", serviceName, exception.getMessage(), exception);
        }
    }
}
