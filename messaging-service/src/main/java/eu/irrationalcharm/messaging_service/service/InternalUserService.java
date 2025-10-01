package eu.irrationalcharm.messaging_service.service;

import eu.irrationalcharm.messaging_service.client.UserServiceClient;
import eu.irrationalcharm.messaging_service.client.dto.UserSocialGraphDto;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternalUserService {

    private final UserServiceClient userServiceClient;


    /**
     * Get user friends list by Identity Provider ID
     * @param idpUUID Identity Provider ID
     * @return UserSocialGraphDto
     */
    @Cacheable(value = "user-graph", key = "#idpUUID")
    public UserSocialGraphDto getUserSocialGraphDto(@NotNull String idpUUID){
        ResponseEntity<UserSocialGraphDto> response = userServiceClient.getUserSocialGraph();

        if(response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }

        throw new RuntimeException("Failed to fetch social graph from user-service");
    }
}
