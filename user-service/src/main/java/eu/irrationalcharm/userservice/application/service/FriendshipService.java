package eu.irrationalcharm.userservice.application.service;

import eu.irrationalcharm.dto.user_service.FriendRequestDto;
import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.enums.ErrorCode;
import eu.irrationalcharm.enums.NotificationType;
import eu.irrationalcharm.enums.SuccessfulCode;
import eu.irrationalcharm.events.FriendRequestEvent;
import eu.irrationalcharm.events.NotificationEvent;
import eu.irrationalcharm.events.UserUpdateEvent;
import eu.irrationalcharm.userservice.application.port.in.ManageFriendshipUseCase;
import eu.irrationalcharm.userservice.application.port.out.FriendRequestRepositoryPort;
import eu.irrationalcharm.userservice.application.port.out.FriendshipPreferenceRepositoryPort;
import eu.irrationalcharm.userservice.application.port.out.FriendshipRepositoryPort;
import eu.irrationalcharm.userservice.application.port.out.NotificationPublisherPort;
import eu.irrationalcharm.userservice.application.port.out.UserEventPublisherPort;
import eu.irrationalcharm.userservice.application.port.out.UserRepositoryPort;
import eu.irrationalcharm.userservice.config.properties.CdnProperties;
import eu.irrationalcharm.userservice.domain.model.FriendRequest;
import eu.irrationalcharm.userservice.domain.model.FriendshipPreference;
import eu.irrationalcharm.userservice.domain.model.User;
import eu.irrationalcharm.userservice.dto.request.UpdateFriendRequestDto;
import eu.irrationalcharm.userservice.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FriendshipService implements ManageFriendshipUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final FriendshipRepositoryPort friendshipRepositoryPort;
    private final FriendRequestRepositoryPort friendRequestRepositoryPort;
    private final FriendshipPreferenceRepositoryPort friendshipPreferenceRepositoryPort;
    private final UserEventPublisherPort userEventPublisherPort;
    private final NotificationPublisherPort notificationPublisherPort;
    private final CdnProperties cdnProperties;

    @Override
    @Transactional(readOnly = true)
    public Set<PublicUserResponseDto> getFriends(java.util.UUID userId) {
        return friendshipRepositoryPort.findFriends(userId).stream()
                .map(user -> UserService.mapToPublicUserDto(user, cdnProperties.baseUrl()))
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendRequestDto> getPendingFriendRequests(java.util.UUID userId) {
        List<FriendRequestDto> rawDtos = friendRequestRepositoryPort.findPendingRequests(userId);
        return rawDtos.stream()
                .map(dto -> dto.toBuilder()
                        .initiatorUrlProfileImage(
                                String.format("%s/%s", cdnProperties.baseUrl(), dto.initiatorUrlProfileImage()))
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void sendFriendRequest(java.util.UUID requestorId, String targetUsername) {
        User requestInitiator = getUserOrThrow(requestorId);
        User requestReceiver = getUserByUsernameOrThrow(targetUsername);

        log.info("Starting process to send friend request from {} to {}", requestInitiator.id(), requestReceiver.id());

        if (isBlocking(requestReceiver.id(), requestInitiator.id())) {
            log.warn("Friend request cancelled: {} is blocking {}", requestReceiver.id(), requestInitiator.id());
            throw new BusinessException(HttpStatus.FORBIDDEN, ErrorCode.FRIEND_REQUEST_BLOCKED_BY_USER,
                    "Cannot send friend request. The recipient has blocked you.");
        }

        if (friendshipRepositoryPort.areFriends(requestInitiator.id(), requestReceiver.id())) {
            log.warn("Friend request cancelled: {} and {} are already friends", requestInitiator.id(), requestReceiver.id());
            throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.FRIEND_REQUEST_ALREADY_FRIENDS,
                    "Cannot send friend request. The recipient is already your friend");
        }

        validateFriendRequestOrThrow(requestInitiator.id(), requestReceiver.id());

        FriendRequest savedFriendRequest = friendRequestRepositoryPort.save(requestInitiator.id(), requestReceiver.id());

        var friendRequestEvent = new FriendRequestEvent(
                savedFriendRequest.id().toString(),
                requestInitiator.id().toString(),
                requestInitiator.username(),
                requestInitiator.displayName(),
                String.format("%s/%s", cdnProperties.baseUrl(), requestInitiator.profileImageUrl()),
                savedFriendRequest.createdAt().toEpochMilli()
        );

        notificationPublisherPort.publishNotification(new NotificationEvent(
                NotificationType.FRIEND_REQUEST_RECEIVED,
                requestReceiver.id().toString(),
                friendRequestEvent
        ));

        log.info("Successfully sent friend request. RequestId: {}", savedFriendRequest.id());
    }

    @Override
    @Transactional
    public void removeFriend(java.util.UUID principalId, String targetUsername) {
        User principal = getUserOrThrow(principalId);
        User friendToBeRemoved = getUserByUsernameOrThrow(targetUsername);

        log.info("Starting process of removing a friend with username {}", targetUsername);

        if (!friendshipRepositoryPort.deleteFriendship(principal.id(), friendToBeRemoved.id())) {
            log.warn("Target username is not friends with {}", principal.id());
            throw new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_NOT_FOUND,
                    String.format("%s is not your friend", targetUsername));
        }

        publishFriendshipChangeEvents(principal.id().toString(), friendToBeRemoved.id().toString());

        log.info("Successfully removed username {} as a friend from {}", targetUsername, principal.username());
    }

    @Override
    @Transactional
    public SuccessfulCode updateFriendRequest(java.util.UUID currentUserId, Long requestId, UpdateFriendRequestDto dto) {
        FriendRequest friendRequest = friendRequestRepositoryPort.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Friend request with ID {} not found", requestId);
                    return new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_REQUEST_NOT_FOUND,
                            String.format("Could not find friend request with id: %s", requestId));
                });

        String receiverId = friendRequest.receiverId().toString();
        String initiatorId = friendRequest.initiatorId().toString();
        String currentUserIdStr = currentUserId.toString();

        if (!receiverId.equals(currentUserIdStr) && !initiatorId.equals(currentUserIdStr)) {
            log.warn("User {} attempted to modify friend request {} which does not belong to them", currentUserId, requestId);
            throw new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_REQUEST_NOT_FOUND,
                    String.format("Could not find friend request with id: %s", requestId));
        }

        log.info("Starting process of updating friend request with requestId: {}", requestId);

        return switch (dto.action()) {
            case ACCEPT_REQUEST -> {
                if (initiatorId.equals(currentUserIdStr)) {
                    throw new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_REQUEST_NOT_FOUND,
                            "Cannot accept your own friend request");
                }
                yield handleAcceptRequest(friendRequest);
            }
            case DECLINE_REQUEST -> {
                if (initiatorId.equals(currentUserIdStr)) {
                    throw new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_REQUEST_NOT_FOUND,
                            "Cannot decline your own friend request, you must CANCEL request");
                }
                friendRequestRepositoryPort.delete(friendRequest.id());
                log.info("Successfully declined friend request with requestId: {}", requestId);
                yield SuccessfulCode.FRIEND_REQUEST_DECLINED;
            }
            case CANCEL_REQUEST -> {
                if (!initiatorId.equals(currentUserIdStr)) {
                    throw new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_REQUEST_NOT_FOUND,
                            "Cannot cancel this friend request, you must DENY request");
                }
                friendRequestRepositoryPort.delete(friendRequest.id());
                log.info("Successfully cancelled friend request with requestId: {}", requestId);
                yield SuccessfulCode.FRIEND_REQUEST_CANCELLED;
            }
        };
    }

    private SuccessfulCode handleAcceptRequest(FriendRequest friendRequest) {
        java.util.UUID receiverId = friendRequest.receiverId();
        java.util.UUID initiatorId = friendRequest.initiatorId();

        friendRequestRepositoryPort.delete(friendRequest.id());
        addFriendOrThrow(receiverId, initiatorId);

        createPreferenceOrThrow(receiverId, initiatorId);
        createPreferenceOrThrow(initiatorId, receiverId);

        publishFriendshipChangeEvents(receiverId.toString(), initiatorId.toString());

        User receiver = getUserOrThrow(receiverId);
        User initiator = getUserOrThrow(initiatorId);
        publishFriendRequestAcceptedEvent(receiverId.toString(), initiator);
        publishFriendRequestAcceptedEvent(initiatorId.toString(), receiver);

        log.info("Successfully accepted friend request");
        return SuccessfulCode.FRIEND_REQUEST_ACCEPTED;
    }

    private void addFriendOrThrow(java.util.UUID userAId, java.util.UUID userBId) {
        if (userAId.compareTo(userBId) == 0)
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.FRIEND_REQUEST_SELF, "Cannot add yourself as friend.");

        if (friendshipRepositoryPort.areFriends(userAId, userBId))
            throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.FRIEND_REQUEST_ALREADY_FRIENDS, "Users are already friends");

        friendshipRepositoryPort.addFriend(userAId, userBId);
    }

    private void createPreferenceOrThrow(java.util.UUID userId, java.util.UUID friendId) {
        if (isBlocking(userId, friendId) || isBlocking(friendId, userId)) {
            log.warn("Cannot create friendship preference: A block exists between {} and {}", userId, friendId);
            throw new BusinessException(HttpStatus.FORBIDDEN, ErrorCode.FRIEND_REQUEST_BLOCKED_BY_USER, "Cannot add friend");
        }

        if (friendshipPreferenceRepositoryPort.findByUserAndFriend(userId, friendId).isEmpty()) {
            FriendshipPreference newPreference = new FriendshipPreference(null, userId, friendId, false, false, false);
            friendshipPreferenceRepositoryPort.save(newPreference);
        }
    }

    private boolean isBlocking(java.util.UUID potentialBlockerId, java.util.UUID potentialBlockedId) {
        return friendshipPreferenceRepositoryPort.findByUserAndFriend(potentialBlockerId, potentialBlockedId)
                .map(FriendshipPreference::blocked)
                .orElse(false);
    }

    private void validateFriendRequestOrThrow(java.util.UUID initiatorId, java.util.UUID receiverId) {
        if (initiatorId.compareTo(receiverId) == 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.FRIEND_REQUEST_SELF,
                    "Cannot send friend request to yourself.");
        }

        if (friendRequestRepositoryPort.findExistingRequest(initiatorId, receiverId).isPresent()) {
            throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.FRIEND_REQUEST_EXISTS,
                    "Friend request already exists.");
        }
    }

    private void publishFriendRequestAcceptedEvent(String recipientId, User newFriend) {
        log.info("Sending Friend Request Accepted event for recipientId: {} regarding new friend: {}", recipientId, newFriend.id());
        PublicUserResponseDto friendDto = UserService.mapToPublicUserDto(newFriend, cdnProperties.baseUrl());
        notificationPublisherPort.publishNotification(new NotificationEvent(
                NotificationType.FRIEND_REQUEST_ACCEPTED,
                recipientId,
                friendDto
        ));
    }

    private void publishFriendshipChangeEvents(String userIdA, String userIdB) {
        log.debug("Publishing UserUpdateEvent to Kafka to evict cache for users {} and {}", userIdA, userIdB);
        userEventPublisherPort.publishUserUpdated(new UserUpdateEvent(userIdA), new UserUpdateEvent(userIdB));
    }

    private User getUserOrThrow(java.util.UUID userId) {
        return userRepositoryPort.findById(userId).orElseThrow(() -> {
            log.warn("Could not find account for user {}", userId);
            return new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.ON_BOARDING_REQUIRED,
                    String.format("Could not find account with this user id: %s", userId));
        });
    }

    private User getUserByUsernameOrThrow(String username) {
        return userRepositoryPort.findByUsername(username).orElseThrow(() -> {
            log.warn("Could not find user with username: {}", username);
            return new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.USERNAME_NOT_FOUND,
                    String.format("User with username %s not found.", username));
        });
    }
}
