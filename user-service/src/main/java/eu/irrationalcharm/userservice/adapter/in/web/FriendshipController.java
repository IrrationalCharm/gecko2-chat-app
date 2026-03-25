package eu.irrationalcharm.userservice.adapter.in.web;

import eu.irrationalcharm.dto.response.SuccessResponseDto;
import eu.irrationalcharm.dto.user_service.FriendRequestDto;
import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.enums.ErrorCode;
import eu.irrationalcharm.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.application.port.in.ManageFriendshipUseCase;
import eu.irrationalcharm.userservice.constants.JwtClaims;
import eu.irrationalcharm.userservice.dto.request.UpdateFriendRequestDto;
import eu.irrationalcharm.userservice.dto.response.base.ApiResponse;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.validation.UsernameValid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendshipController {

    private final ManageFriendshipUseCase manageFriendshipUseCase;

    @GetMapping
    public ResponseEntity<SuccessResponseDto<Set<PublicUserResponseDto>>> getFriends(
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest request) {

        UUID userId = extractUserIdOrThrow(jwt);
        Set<PublicUserResponseDto> friendsDto = manageFriendshipUseCase.getFriends(userId);

        return ApiResponse.success(HttpStatus.OK, SuccessfulCode.FRIENDS_LIST, "List of users friends", friendsDto, request);
    }

    @GetMapping("/requests")
    public ResponseEntity<SuccessResponseDto<List<FriendRequestDto>>> pendingFriendRequests(
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest request) {

        UUID userId = extractUserIdOrThrow(jwt);
        List<FriendRequestDto> pendingFriendRequests = manageFriendshipUseCase.getPendingFriendRequests(userId);

        return ApiResponse.success(HttpStatus.OK, SuccessfulCode.FRIEND_REQUEST_PENDING,
                "List of pending friend requests", pendingFriendRequests, request);
    }

    @PostMapping("/requests/{username}")
    public ResponseEntity<SuccessResponseDto<Void>> sendFriendRequest(
            @PathVariable @UsernameValid String username,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest request) {

        UUID userId = extractUserIdOrThrow(jwt);
        log.info("Received request to send friend request to target username: {}", username);
        manageFriendshipUseCase.sendFriendRequest(userId, username);

        return ApiResponse.success(HttpStatus.CREATED, SuccessfulCode.FRIEND_REQUEST_SENT,
                "Friend request sent successfully", null, request);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<SuccessResponseDto<Void>> removeFriend(
            @PathVariable @UsernameValid String username,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest request) {

        UUID userId = extractUserIdOrThrow(jwt);
        log.info("Received request to delete friend with username: {}", username);
        manageFriendshipUseCase.removeFriend(userId, username);

        return ApiResponse.success(HttpStatus.OK, SuccessfulCode.FRIEND_REMOVED,
                String.format("Successfully removed %s as friend", username), null, request);
    }

    @PatchMapping("/requests/{requestId}")
    public ResponseEntity<SuccessResponseDto<PublicUserResponseDto>> updateFriendRequest(
            @PathVariable @Positive Long requestId,
            @RequestBody @Valid UpdateFriendRequestDto friendRequestDto,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest request) {

        UUID userId = extractUserIdOrThrow(jwt);
        log.info("Received request to update friend request status of request id: {}", requestId);

        SuccessfulCode successfulCode = manageFriendshipUseCase.updateFriendRequest(userId, requestId, friendRequestDto);

        return ApiResponse.success(HttpStatus.OK, successfulCode, "Friend request has been updated", null, request);
    }

    private UUID extractUserIdOrThrow(Jwt jwt) {
        String userIdStr = jwt.getClaimAsString(JwtClaims.INTERNAL_ID);
        if (userIdStr == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.ON_BOARDING_REQUIRED,
                    "Empty internal_id in claim");
        }
        return UUID.fromString(userIdStr);
    }
}
