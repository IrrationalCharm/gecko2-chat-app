package eu.irrationalcharm.messaging_service.client;


import eu.irrationalcharm.messaging_service.client.dto.UserSocialGraphDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "user-service", url = "${spring.feign.client.user-service.v1.base-url}")
public interface UserServiceClient {

    @Retry(name = "social-graph")
    //@CircuitBreaker(name = "social-graph", fallbackMethod = "getAuthenticatedUserSocialGraphFallback")
    @GetMapping("/social-graph")
    ResponseEntity<UserSocialGraphDto> getAuthenticatedUserSocialGraph();


    @Retry(name = "social-graph-username")
    //@CircuitBreaker(name = "social-graph-username")
    @GetMapping("/social-graph/user/{username}")
    ResponseEntity<UserSocialGraphDto> getUserSocialGraphByUsername(@PathVariable String username);
}
