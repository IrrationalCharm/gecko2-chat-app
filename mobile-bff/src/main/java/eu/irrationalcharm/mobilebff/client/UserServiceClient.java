package eu.irrationalcharm.mobilebff.client;

import eu.irrationalcharm.dto.user_service.FriendRequestDto;
import eu.irrationalcharm.dto.response.SuccessResponseDto;
import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.dto.user_service.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;
import java.util.Set;

@HttpExchange(url = "${url.user-service}", accept = "application/json")
public interface UserServiceClient {

    @GetExchange("/api/v1/friends")
    ResponseEntity<SuccessResponseDto<Set<PublicUserResponseDto>>> getFriends();

    @GetExchange("/api/v1/users/me")
    ResponseEntity<SuccessResponseDto<UserDto>> fetchMe();

    @GetExchange("/api/v1/friends/requests")
    ResponseEntity<SuccessResponseDto<List<FriendRequestDto>>> pendingFriendRequests();
}
