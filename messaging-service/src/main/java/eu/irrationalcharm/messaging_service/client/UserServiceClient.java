package eu.irrationalcharm.messaging_service.client;


import eu.irrationalcharm.messaging_service.client.dto.UserSocialGraphDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service", url = "http://localhost:8082/internal/api/v1")
public interface UserServiceClient {

    @GetMapping("/social-graph")
    ResponseEntity<UserSocialGraphDto> getUserSocialGraph();
}
