package eu.irrationalcharm.userservice.controller;



import eu.irrationalcharm.userservice.annotation.UsernameValid;
import eu.irrationalcharm.userservice.dto.request.UpdateFriendRequestDto;
import eu.irrationalcharm.userservice.dto.response.PublicUserResponseDto;
import eu.irrationalcharm.userservice.dto.response.base.ApiResponse;
import eu.irrationalcharm.userservice.dto.response.base.SuccessResponseDto;
import eu.irrationalcharm.userservice.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.service.FriendRequestService;
import eu.irrationalcharm.userservice.service.FriendshipOrchestrator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * Here we will manage anything related to friend requests, friend lists...
 */
@Validated
@RestController
@RequestMapping("/api/v1/friends")
@AllArgsConstructor
public class FriendshipController {

    private final FriendRequestService friendRequestService;
    private final FriendshipOrchestrator friendshipOrchestrator;


    @GetMapping
    public ResponseEntity<SuccessResponseDto<Object>> getFriends(@AuthenticationPrincipal Jwt jwt,
                                                                 HttpServletRequest request) {
        Set<PublicUserResponseDto> friendsDto = friendshipOrchestrator.getFriends(jwt);

        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.FRIENDS_LIST,
                "List of users friends",
                friendsDto,
                request
        );
    }


    @PostMapping("/requests/{username}")
    public ResponseEntity<SuccessResponseDto<Object>> sendFriendRequest(@PathVariable("username") @UsernameValid String username,
                                                                        @AuthenticationPrincipal Jwt jwt,
                                                                        HttpServletRequest request) {
        friendshipOrchestrator.sendFriendRequest(jwt, username);

        return ApiResponse.success(
                HttpStatus.CREATED,
                SuccessfulCode.FRIEND_REQUEST_SENT,
                "Friend request sent successfully",
                null,
                request
        );
    }


    @GetMapping("/requests")
    public ResponseEntity<SuccessResponseDto< List<PublicUserResponseDto> >> pendingFriendRequests(@AuthenticationPrincipal Jwt jwt, HttpServletRequest request) {

        List<PublicUserResponseDto> pendingFriendRequests = friendRequestService.getPendingFriendRequests(jwt);

        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.FRIEND_REQUEST_PENDING,
                "List of pending friend requests",
                pendingFriendRequests,
                request
        );
    }


    @PatchMapping("/requests/{username}")
    public ResponseEntity<SuccessResponseDto<Object>> updateFriendRequest(@PathVariable @UsernameValid String username,
                                                                          @RequestBody @Valid UpdateFriendRequestDto friendRequestDto,
                                                                          @AuthenticationPrincipal Jwt jwt,
                                                                          HttpServletRequest request) {

        SuccessfulCode successfulCode = friendshipOrchestrator.updateFriendRequest(jwt, friendRequestDto, username);

        return ApiResponse.success(
                HttpStatus.OK,
                successfulCode,
                "Friend request has been updated",
                null,
                request
        );
    }
}
