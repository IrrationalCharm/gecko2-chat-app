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
        return userServiceClient.getFriends().getBody().data();
    }

    @Retry(name = "fetch-me")
    @CircuitBreaker(name = "fetch-me", fallbackMethod = "fetchMeFallback")
    public Set<PublicUserResponseDto> getFriendsFallback(Throwable t) {
        log.warn("Fallback triggered for sync conversation: {}", t.getMessage());
        return Collections.emptySet();
    }


    @Retry(name = "fetch-me")
    @CircuitBreaker(name = "fetch-me", fallbackMethod = "fetchMeFallback")
    public UserDto fetchMe() {
        return userServiceClient.fetchMe().getBody().data();
    }

    public Set<PublicUserResponseDto> fetchMeFallback(Throwable t) {
        log.warn("Fallback triggered for fetch me: {}", t.getMessage());
        return Collections.emptySet();
    }

    @Retry(name = "pending-requests")
    @CircuitBreaker(name = "pending-requests", fallbackMethod = "pendingFriendRequestsFallback")
    public List<FriendRequestDto> pendingFriendRequests() {
        return  userServiceClient.pendingFriendRequests().getBody().data();
    }

    public List<FriendRequestDto> pendingFriendRequestsFallback(Throwable t) {
        log.warn("Fallback triggered for pending-requests: {}", t.getMessage());
        return Collections.emptyList();
    }
}
