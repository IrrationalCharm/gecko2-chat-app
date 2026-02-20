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

import java.util.List;
import java.util.Set;

@HttpExchange(url = "${url.user-service}", accept = "application/json")
public interface UserServiceClient {

    @Retry(name = "get-friends")
    @CircuitBreaker(name = "get-friends")
    @GetExchange("/api/v1/friends")
    ResponseEntity<SuccessResponseDto<Set<PublicUserResponseDto>>> getFriends();

    @Retry(name = "fetch-me")
    @CircuitBreaker(name = "fetch-me")
    @GetExchange("/api/v1/users/me")
    ResponseEntity<SuccessResponseDto<UserDto>> fetchMe();

    @Retry(name = "pending-requests")
    @CircuitBreaker(name = "pending-requests")
    @GetExchange("/api/v1/friends/requests")
    ResponseEntity<SuccessResponseDto<List<FriendRequestDto>>> pendingFriendRequests();
}
