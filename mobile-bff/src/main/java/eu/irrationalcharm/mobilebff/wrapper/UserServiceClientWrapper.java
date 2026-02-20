package eu.irrationalcharm.mobilebff.wrapper;

import eu.irrationalcharm.dto.response.SuccessResponseDto;
import eu.irrationalcharm.dto.user_service.FriendRequestDto;
import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.dto.user_service.UserDto;
import eu.irrationalcharm.mobilebff.client.UserServiceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceClientWrapper {

    private final UserServiceClient userServiceClient;


    @Retry(name = "get-friends")
    @CircuitBreaker(name = "get-friends", fallbackMethod = "getFriendsFallback")
    public Set<PublicUserResponseDto> getFriends() {
        var response = userServiceClient.getFriends();
        if (response != null && response.getBody() != null) {
            return response.getBody().data();
        }

        log.warn("response / body was null, returning empty friends list");
        return Collections.emptySet();
    }


    public Set<PublicUserResponseDto> getFriendsFallback(Throwable t) {
        log.warn("Fallback triggered for sync conversation: {}", t.getMessage());
        return Collections.emptySet();
    }


    @Nullable
    @Retry(name = "fetch-me")
    @CircuitBreaker(name = "fetch-me", fallbackMethod = "fetchMeFallback")
    public UserDto fetchMe() {
        var response = userServiceClient.fetchMe();
        if (response != null && response.getBody() != null) {
            return response.getBody().data();
        }

        log.warn("response / body was null, returning null user details");
        return null;
    }

    public UserDto fetchMeFallback(Throwable t) {
        log.warn("Fallback triggered for fetch me: {}", t.getMessage());
        return null;
    }

    @Retry(name = "pending-requests")
    @CircuitBreaker(name = "pending-requests", fallbackMethod = "pendingFriendRequestsFallback")
    public List<FriendRequestDto> pendingFriendRequests() {
        var response = userServiceClient.pendingFriendRequests();
        if (response != null && response.getBody() != null) {
            return response.getBody().data();
        }

        log.warn("response / body was null, returning null pending requests");
        return Collections.emptyList();
    }

    public List<FriendRequestDto> pendingFriendRequestsFallback(Throwable t) {
        log.warn("Fallback triggered for pending-requests: {}", t.getMessage());
        return Collections.emptyList();
    }
}
