package eu.irrationalcharm.userservice.service.orchestrator;

import eu.irrationalcharm.events.FriendRequestEvent;
import eu.irrationalcharm.enums.NotificationType;
import eu.irrationalcharm.events.NotificationEvent;
import eu.irrationalcharm.userservice.config.properties.CdnProperties;
import eu.irrationalcharm.userservice.constants.JwtClaims;
import eu.irrationalcharm.userservice.dto.request.UpdateFriendRequestDto;
import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.userservice.entity.FriendRequestEntity;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.enums.ErrorCode;
import eu.irrationalcharm.enums.SuccessfulCode;
import eu.irrationalcharm.events.UserUpdateEvent;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.userservice.mapper.UserMapper;
import eu.irrationalcharm.userservice.service.*;
import eu.irrationalcharm.userservice.service.event.NotificationProducer;
import eu.irrationalcharm.userservice.service.event.UserEventProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class FriendshipOrchestrator {

    private final FriendshipService friendshipService;
    private final FriendRequestService friendRequestService;
    private final UserFriendshipPreferenceService friendPreferenceService;
    private final UserService userService;
    private final UserEventProducer userEventProducer;
    private final NotificationProducer notificationProducer;
    private final CdnProperties cdnProperties;


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

        FriendRequestEntity savedFriendRequest = friendRequestService.sendFriendRequestOrThrow(requestInitiator, requestReceiver);

        //Publish event to Kafka
        var friendRequestEvent = new FriendRequestEvent(
                savedFriendRequest.getId().toString(),
                requestInitiator.getId().toString(),
                requestInitiator.getUsername(),
                requestInitiator.getDisplayName(),
                String.format("%s/%s",cdnProperties.baseUrl(), requestInitiator.getProfileImageUrl()),
                savedFriendRequest.getCreated_at().toEpochMilli()
        );

        var notificationEvent = new NotificationEvent(
                NotificationType.FRIEND_REQUEST_RECEIVED,
                requestReceiver.getId().toString(),
                friendRequestEvent
                );

        notificationProducer.publishNotificationEvent(notificationEvent);
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
    public SuccessfulCode updateFriendRequest(Jwt jwt, Long requestId, UpdateFriendRequestDto friendRequestDto) {
        userService.isAuthenticatedOnBoardedOrThrow(jwt);
        FriendRequestEntity friendRequest = friendRequestService.getFriendRequestOrThrow(requestId);

        String currentAuthUserId = jwt.getClaimAsString(JwtClaims.INTERNAL_ID);

        String receiverId = friendRequest.getReceiver().getId().toString();
        String initiatorId = friendRequest.getInitiator().getId().toString();

        if (!receiverId.equals(currentAuthUserId) && !initiatorId.equals(currentAuthUserId)) {
            throw new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_REQUEST_NOT_FOUND, String.format("Could not find friend request with id: %s", requestId));
        }

        return switch (friendRequestDto.action()) {
            case ACCEPT_REQUEST -> {
                if (initiatorId.equals(currentAuthUserId))
                    throw new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_REQUEST_NOT_FOUND, "Cannot accept your own friend request");

                yield handleAcceptRequest(friendRequest);
            }

            case DECLINE_REQUEST -> {
                if (initiatorId.equals(currentAuthUserId))
                    throw new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_REQUEST_NOT_FOUND, "Cannot decline your own friend request, you must CANCEL request");

                friendRequestService.deleteFriendRequestOrThrow(friendRequest); //We have to validate it belongs to user
                yield SuccessfulCode.FRIEND_REQUEST_DECLINED;
            }

            case CANCEL_REQUEST -> {
                if (!initiatorId.equals(currentAuthUserId))
                    throw new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_REQUEST_NOT_FOUND, "Cannot cancel this friend request, you must DENY request");

                friendRequestService.deleteFriendRequestOrThrow(friendRequest);
                yield SuccessfulCode.FRIEND_REQUEST_CANCELLED;
            }
        };

    }


    //  PRIVATE HELPER METHODS  \\
    private SuccessfulCode handleAcceptRequest(FriendRequestEntity friendRequest) {
        UserEntity userReceiver = friendRequest.getReceiver();
        UserEntity userInitiator = friendRequest.getInitiator();

        friendRequestService.deleteFriendRequestOrThrow(friendRequest);
        friendshipService.addFriendOrThrow(userReceiver, userInitiator);

        friendPreferenceService.createNewFriendshipPreferenceEntityOrThrow(userReceiver.getId(), userInitiator.getId());
        friendPreferenceService.createNewFriendshipPreferenceEntityOrThrow(userInitiator.getId(), userReceiver.getId());

        //Notifies messaging-service to evict outdated cache
        publishFriendshipChangeEvents(userReceiver, userInitiator);

        //Notifies both user of a friend request accepted.
        publishFriendRequestAcceptedEvent(userReceiver.getId().toString(), userInitiator);
        publishFriendRequestAcceptedEvent(userInitiator.getId().toString(), userReceiver);

        return SuccessfulCode.FRIEND_REQUEST_ACCEPTED;
    }


    /**
     * Sends an event to messaging-service to notify (if user is connected) that a friend request is accepted
     */
    private void publishFriendRequestAcceptedEvent(String recipientId , UserEntity newFriend) {
        log.info("Sending Friend Request Accepted event to kafka");
        PublicUserResponseDto friendDto = UserMapper.mapToPublicUserDto(newFriend, cdnProperties.baseUrl());
        var event = new NotificationEvent(
                NotificationType.FRIEND_REQUEST_ACCEPTED,
                recipientId,
                friendDto);

        notificationProducer.publishNotificationEvent(event);
    }


    /**
     * Sends an update event to messaging-service to evict outdated cached data
     */
    private void publishFriendshipChangeEvents(UserEntity userA, UserEntity userB) {
        String userIdA = userA.getId().toString();
        String userIdB = userB.getId().toString();

        var userAUpdateEvent = new UserUpdateEvent(userIdA);
        var userBUpdateEvent = new UserUpdateEvent(userIdB);

        userEventProducer.publishUserUpdatedEvent(userAUpdateEvent, userBUpdateEvent);
    }
}
