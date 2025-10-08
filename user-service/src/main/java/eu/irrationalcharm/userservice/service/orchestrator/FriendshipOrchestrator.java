package eu.irrationalcharm.userservice.service.orchestrator;

import eu.irrationalcharm.userservice.dto.request.UpdateFriendRequestDto;
import eu.irrationalcharm.userservice.dto.response.PublicUserResponseDto;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.userservice.enums.ErrorCode;
import eu.irrationalcharm.userservice.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.event.UserUpdateEvent;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.userservice.service.*;
import eu.irrationalcharm.userservice.service.event.UserEventProducer;
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
    private final UserEventProducer userEventProducer;


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


    @Transactional
    public void removeFriend(Jwt jwt, String username) {
        UserEntity principal = userService.getAuthenticatedEntityOrThrow(jwt);
        UserEntity friendToBeRemoved = userService.getEntityByUsernameOrThrow(username);

        if (!friendshipService.removeFriend(principal, friendToBeRemoved)) {
            throw new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_NOT_FOUND, String.format("%s is not your friend", username));
        }

        publishFriendshipChangeEvents(principal, friendToBeRemoved);
    }


    @Transactional
    public SuccessfulCode updateFriendRequest(Jwt jwt, UpdateFriendRequestDto friendRequestDto, String username) {
        UserEntity requestUpdater = userService.getAuthenticatedEntityOrThrow(jwt);
        UserEntity requestRecipient = userService.getEntityByUsernameOrThrow(username);

        return switch (friendRequestDto.action()) {
            case ACCEPT_REQUEST -> handleAcceptRequest(requestRecipient, requestUpdater);
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


    //  PRIVATE HELPER METHODS  \\


    private SuccessfulCode handleAcceptRequest(UserEntity requestRecipient, UserEntity requestUpdater) {
        friendRequestService.deleteFriendRequestOrThrow(requestRecipient, requestUpdater);
        friendshipService.addFriendOrThrow(requestRecipient, requestUpdater);

        friendPreferenceService.createNewFriendshipPreferenceEntityOrThrow(requestRecipient.getId(), requestUpdater.getId());
        friendPreferenceService.createNewFriendshipPreferenceEntityOrThrow(requestUpdater.getId(), requestRecipient.getId());

        publishFriendshipChangeEvents(requestRecipient, requestUpdater);

        return SuccessfulCode.FRIEND_REQUEST_ACCEPTED;
    }


    /**
     * Sends an update event to messaging service to evict outdated cached data
     */
    private void publishFriendshipChangeEvents(UserEntity userA, UserEntity userB) {
        String userIdA = userA.getId().toString();
        String userIdB = userB.getId().toString();

        var userAUpdateEvent = new UserUpdateEvent(userIdA);
        var userBUpdateEvent = new UserUpdateEvent(userIdB);

        userEventProducer.publishUserUpdatedEvent(userAUpdateEvent, userBUpdateEvent);
    }
}
