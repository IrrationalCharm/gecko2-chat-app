package eu.irrationalcharm.userservice.service;

import eu.irrationalcharm.userservice.dto.request.UpdateFriendRequestDto;
import eu.irrationalcharm.userservice.dto.response.PublicUserResponseDto;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.userservice.enums.ErrorCode;
import eu.irrationalcharm.userservice.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@AllArgsConstructor
public class FriendshipOrchestrator {

    private final FriendshipService friendshipService;
    private final FriendRequestService friendRequestService;
    private final UserFriendshipPreferenceService friendPreferenceService;
    private final UserService userService;


    @Transactional(readOnly = true)
    public Set<PublicUserResponseDto> getFriends(Jwt jwt) {
        UserEntity userEntity = userService.getAuthenticatedEntityOrThrow(jwt);

        return friendshipService.getFriends(userEntity);
    }


    @Transactional
    public void sendFriendRequest(Jwt jwt, String username) {
        UserEntity requestInitiator = userService.getAuthenticatedEntityOrThrow(jwt);
        UserEntity requestReceiver = userService.getEntityByUsernameOrThrow(username);

        if (friendPreferenceService.isBlocking(requestReceiver.getId(), requestInitiator.getId()))
            throw new BusinessException(HttpStatus.FORBIDDEN, ErrorCode.FRIEND_REQUEST_BLOCKED_BY_USER, "Cannot send friend request. The recipient has blocked you.");

        if (friendshipService.areFriends(requestInitiator, requestReceiver))
            throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.FRIEND_REQUEST_ALREADY_FRIENDS, "Cannot send friend request. The recipient is already your friend");

        friendRequestService.sendFriendRequestOrThrow(requestInitiator, requestReceiver);
    }


    public SuccessfulCode updateFriendRequest(Jwt jwt, UpdateFriendRequestDto friendRequestDto, String username) {
        UserEntity requestUpdater = userService.getAuthenticatedEntityOrThrow(jwt);
        UserEntity requestRecipient = userService.getEntityByUsernameOrThrow(username);

        return switch (friendRequestDto.action()) {
            case ACCEPT_REQUEST -> {
                friendRequestService.deleteFriendRequestOrThrow(requestRecipient, requestUpdater);
                friendshipService.addFriendOrThrow(requestRecipient, requestUpdater);

                friendPreferenceService.createNewFriendshipPreferenceEntityOrThrow(requestRecipient.getId(), requestUpdater.getId());
                friendPreferenceService.createNewFriendshipPreferenceEntityOrThrow(requestUpdater.getId(), requestRecipient.getId());

                yield SuccessfulCode.FRIEND_REQUEST_ACCEPTED;
            }
            case DECLINE_REQUEST -> {
                friendRequestService.deleteFriendRequestOrThrow(requestRecipient, requestUpdater);
                yield SuccessfulCode.FRIEND_REQUEST_DECLINED;
            }
            case CANCEL_REQUEST -> {
                friendRequestService.deleteFriendRequestOrThrow(requestUpdater, requestRecipient);
                yield SuccessfulCode.FRIEND_REQUEST_CANCELLED;
            }
        };

    }
}
