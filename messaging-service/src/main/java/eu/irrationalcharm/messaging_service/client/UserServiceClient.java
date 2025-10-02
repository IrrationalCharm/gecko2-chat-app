package eu.irrationalcharm.messaging_service.client;


import eu.irrationalcharm.messaging_service.client.dto.UserSocialGraphDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8082/user-service/internal/api/v1")
public interface UserServiceClient {

    @GetMapping("/social-graph")
    ResponseEntity<UserSocialGraphDto> getAuthenticatedUserSocialGraph();

    @GetMapping("/social-graph/user/{username}")
    ResponseEntity<UserSocialGraphDto> getUserSocialGraphByUsername(@PathVariable String username);
}
