package eu.irrationalcharm.userservice.controller;



import eu.irrationalcharm.dto.response.SuccessResponseDto;
import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.dto.request.UpdateFriendRequestDto;
import eu.irrationalcharm.dto.user_service.FriendRequestDto;
import eu.irrationalcharm.userservice.dto.response.base.ApiResponse;
import eu.irrationalcharm.userservice.service.FriendRequestService;
import eu.irrationalcharm.userservice.service.orchestrator.FriendshipOrchestrator;
import eu.irrationalcharm.validation.UsernameValid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendRequestService friendRequestService;
    private final FriendshipOrchestrator friendshipOrchestrator;


    @GetMapping
    public ResponseEntity<SuccessResponseDto< Set<PublicUserResponseDto> >> getFriends(@AuthenticationPrincipal Jwt jwt,
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


    @GetMapping("/requests")
    public ResponseEntity<SuccessResponseDto< List<FriendRequestDto> >> pendingFriendRequests(@AuthenticationPrincipal Jwt jwt,
                                                                                                   HttpServletRequest request) {

        List<FriendRequestDto> pendingFriendRequests = friendRequestService.getPendingFriendRequests(jwt);

        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.FRIEND_REQUEST_PENDING,
                "List of pending friend requests",
                pendingFriendRequests,
                request
        );
    }


    //TODO: Update to use user internalId over username
    @PostMapping("/requests/{username}")
    public ResponseEntity<SuccessResponseDto<Void>> sendFriendRequest(@PathVariable String username,
                                                                        @AuthenticationPrincipal Jwt jwt,
                                                                        HttpServletRequest request) {
        log.info("Received request to send friend request to target username: {}", username);
        friendshipOrchestrator.sendFriendRequest(jwt, username);


        return ApiResponse.success(
                HttpStatus.CREATED,
                SuccessfulCode.FRIEND_REQUEST_SENT,
                "Friend request sent successfully",
                null,
                request
        );
    }


    //TODO: Update to use user internalId over username
    @DeleteMapping("/{username}")
    public ResponseEntity<SuccessResponseDto<Void>> removeFriend(@PathVariable @UsernameValid String username,
                                                                   @AuthenticationPrincipal Jwt jwt,
                                                                   HttpServletRequest request) {
        log.info("Received request to delete friend request to target username: {}", username);
        friendshipOrchestrator.removeFriend(jwt, username);


        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.FRIEND_REMOVED,
                String.format("Successfully removed %s as friend", username),
                null,
                request
        );
    }


    @PatchMapping("/requests/{requestId}")
    public ResponseEntity<SuccessResponseDto<PublicUserResponseDto>> updateFriendRequest(@PathVariable Long requestId,
                                                                        @RequestBody @Valid UpdateFriendRequestDto friendRequestDto,
                                                                        @AuthenticationPrincipal Jwt jwt,
                                                                        HttpServletRequest request) {
        log.info("Received request to update friend request status of request id: {}", requestId);

        SuccessfulCode successfulCode = friendshipOrchestrator.updateFriendRequest(jwt, requestId, friendRequestDto);

        return ApiResponse.success(
                HttpStatus.OK,
                successfulCode,
                "Friend request has been updated",
                null,
                request
        );
    }
}
