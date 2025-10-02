package eu.irrationalcharm.messaging_service.service;

import eu.irrationalcharm.messaging_service.client.UserServiceClient;
import eu.irrationalcharm.messaging_service.client.dto.UserSocialGraphDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class InternalUserService {

    private final UserServiceClient userServiceClient;
    private final Cache userGraphCache;
    private static final String USER_GRAPH_PREFIX = "user-graph";


    public InternalUserService(UserServiceClient userServiceClient, CacheManager cache) {
        this.userServiceClient = userServiceClient;
        this.userGraphCache = cache.getCache(USER_GRAPH_PREFIX);
    }

    /**
     * Checks cache first, on miss it calls user-service and retrieves UserSocialGraphDto by JWT details.
     * providerId is just the key for the cache.
     * The reason we cache twice by different keys is that on CONNECT, the JWT only has the providerId and not the username.
     */
    public UserSocialGraphDto getUserSocialGraphByProviderId(@NotNull String providerId){
        UserSocialGraphDto userSocialGraphDto = userGraphCache.get(providerId, UserSocialGraphDto.class);
        if(userSocialGraphDto != null) {
            return userSocialGraphDto;
        }

        ResponseEntity<UserSocialGraphDto> response = userServiceClient.getAuthenticatedUserSocialGraph();
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            UserSocialGraphDto userGraph = response.getBody();

            userGraphCache.put(providerId, response.getBody());
            userGraphCache.put(userGraph.username(), response.getBody());

            return response.getBody();
        } else
            throw new RuntimeException("Something went wrong!!!");
    }


    /**
     * Checks cache, on miss it fetches it from user-service by username.
     */
    public UserSocialGraphDto getUserSocialGraphByUsername(@NotNull String username){
        UserSocialGraphDto userSocialGraphDto = userGraphCache.get(username, UserSocialGraphDto.class);
        if(userSocialGraphDto != null) {
            return userSocialGraphDto;
        }

        ResponseEntity<UserSocialGraphDto> response = userServiceClient.getUserSocialGraphByUsername(username);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            userGraphCache.put(username, response.getBody());
            return response.getBody();
        } else
            throw new RuntimeException("Something went wrong!!!");
    }


    /**
     * Do not use! just for reference.
     */
    @CachePut(value = "user-graph", key = "#result.username")
    public UserSocialGraphDto fetchAndCacheUserSocialGraph(@NotNull String idpUuid) {
        // Note: The idpUuid is passed implicitly via the Feign interceptors Authorization header.
        ResponseEntity<UserSocialGraphDto> response = userServiceClient.getAuthenticatedUserSocialGraph();

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }

        throw new RuntimeException("Failed to fetch social graph from user-service");
    }
}
