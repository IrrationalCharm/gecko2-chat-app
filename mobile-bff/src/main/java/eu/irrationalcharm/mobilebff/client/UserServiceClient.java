package eu.irrationalcharm.mobilebff.client;

import eu.irrationalcharm.dto.user_service.FriendRequestDto;
import eu.irrationalcharm.dto.response.SuccessResponseDto;
import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.dto.user_service.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@HttpExchange(url = "${url.user-service}", accept = "application/json")
public interface UserServiceClient {

    @Retry(name = "get-friends")
    @CircuitBreaker(name = "get-friends", fallbackMethod = "getFriendsFallback")
    @GetExchange("/api/v1/friends")
    ResponseEntity<SuccessResponseDto<Set<PublicUserResponseDto>>> getFriends();

    default ResponseEntity<SuccessResponseDto<Set<PublicUserResponseDto>>> getFriendsFallback(Throwable t) {
        return ResponseEntity.ok(SuccessResponseDto.<Set<PublicUserResponseDto>>builder()
                .status(200)
                .code("FALLBACK_FRIENDS_LIST") // Optional: a custom code to let the frontend know this is cached/fallback data
                .detail("Could not retrieve friends list at this time.")
                .data(Collections.emptySet())
                .build());
    }

    @Retry(name = "fetch-me")
    @CircuitBreaker(name = "fetch-me", fallbackMethod = "fetchMeFallback")
    @GetExchange("/api/v1/users/me")
    ResponseEntity<SuccessResponseDto<UserDto>> fetchMe();

    default ResponseEntity<SuccessResponseDto<UserDto>> fetchMeFallback(Throwable t) {
        return ResponseEntity.ok(SuccessResponseDto.<UserDto>builder()
                .status(200)
                .code("FALLBACK_FETCH_ME") // Optional: a custom code to let the frontend know this is cached/fallback data
                .detail("Could not retrieve user details at this time.")
                .data(null)
                .build());
    }

    @Retry(name = "pending-requests")
    @CircuitBreaker(name = "pending-requests", fallbackMethod = "pendingFriendRequestsFallback")
    @GetExchange("/api/v1/friends/requests")
    ResponseEntity<SuccessResponseDto<List<FriendRequestDto>>> pendingFriendRequests();

    default ResponseEntity<SuccessResponseDto<List<FriendRequestDto>>> pendingFriendRequestsFallback(Throwable t) {
        return ResponseEntity.ok(SuccessResponseDto.<List<FriendRequestDto>>builder()
                .status(200)
                .code("FALLBACK_REQUESTS") // Optional: a custom code to let the frontend know this is cached/fallback data
                .detail("Could not retrieve friend requests at this time.")
                .data(Collections.emptyList())
                .build());
    }
}
