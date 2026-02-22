package eu.irrationalcharm.userservice.service;

import eu.irrationalcharm.dto.user_service.FriendRequestDto;
import eu.irrationalcharm.userservice.entity.FriendRequestEntity;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.enums.ErrorCode;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.userservice.repository.FriendRequestRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class FriendRequestService {

    private final UserService userService;
    private final FriendRequestRepository friendRequestRepository;


    @Transactional
    public FriendRequestEntity sendFriendRequestOrThrow(UserEntity requestInitiator, UserEntity requestReceiver) {

        validateFriendRequestOrThrow(requestInitiator.getId(), requestReceiver.getId());

        var friendRequest = new FriendRequestEntity();
        friendRequest.setInitiator(requestInitiator);
        friendRequest.setReceiver(requestReceiver);

        return friendRequestRepository.save(friendRequest);
    }


    private void validateFriendRequestOrThrow(UUID requestInitiator, UUID requestReceiver) {
        if (requestInitiator.compareTo(requestReceiver) == 0) {
            log.warn("Validation failed: User {} attempted to send a friend request to themselves", requestInitiator);
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.FRIEND_REQUEST_SELF, "Cannot send friend request to yourself.");
        }

        var friendRequestOptional = friendRequestRepository.findExistingRequestsBetweenUsers(requestInitiator, requestReceiver);
        if (friendRequestOptional.isPresent()) {
            log.warn("Validation failed: A friend request already exists between {} and {}", requestInitiator, requestReceiver);
            throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.FRIEND_REQUEST_EXISTS, "Friend request already exists.");
        }
    }


    @Transactional(readOnly = true)
    public List<FriendRequestDto> getPendingFriendRequests(Jwt jwt) {
        UserEntity userEntity = userService.getAuthenticatedEntityOrThrow(jwt);

        return friendRequestRepository.findPendingFriendRequestsAsDto(userEntity.getId());
    }


    @Transactional(readOnly = true)
    public FriendRequestEntity getFriendRequestOrThrow(UserEntity requestInitiator, UserEntity requestReceiver) {
        return friendRequestRepository.findByInitiatorAndReceiver(requestInitiator, requestReceiver)
                .orElseThrow(() -> {
                    log.warn("Friend request not found between initiator {} and receiver {}", requestInitiator.getId(), requestReceiver.getId());
                    return new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_REQUEST_NOT_FOUND, "Friend request doesn't exist");
                });
    }

    public FriendRequestEntity getFriendRequestOrThrow(Long friendRequestId) {
        return friendRequestRepository.findById(friendRequestId)
                .orElseThrow(() -> {
                    log.warn("Friend request with ID {} not found in database", friendRequestId);
                    return new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_REQUEST_NOT_FOUND, "Friend request doesn't exist");
                });
    }


    /**
     * removes any remaining friend requests on both sides
     */
    public void removeFriendRequestBetweenUsers(UUID userId, UUID toBeBlockedUserId) {
        friendRequestRepository.findExistingRequestsBetweenUsers(userId, toBeBlockedUserId)
                .ifPresent(request -> {
                    log.debug("Found existing friend request (ID: {}) between {} and {}. Deleting it.", request.getId(), userId, toBeBlockedUserId);
                    friendRequestRepository.delete(request);
                });
    }


    @Transactional
    public void deleteFriendRequestOrThrow(UserEntity requestCanceler, UserEntity requestRecipient) {
        FriendRequestEntity friendRequest = getFriendRequestOrThrow(requestCanceler, requestRecipient);

        friendRequestRepository.delete(friendRequest);
    }

    @Transactional
    public void deleteFriendRequestOrThrow(FriendRequestEntity friendRequest) {
        friendRequestRepository.delete(friendRequest);
    }
}
