package eu.irrationalcharm.userservice.service;

import eu.irrationalcharm.userservice.dto.response.PublicUserResponseDto;
import eu.irrationalcharm.userservice.entity.FriendRequestEntity;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.userservice.enums.ErrorCode;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.userservice.repository.FriendRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FriendRequestService {

    private final UserService userService;
    private final FriendRequestRepository friendRequestRepository;


    @Transactional
    public void sendFriendRequestOrThrow(UserEntity requestInitiator, UserEntity requestReceiver) {

        validateFriendRequestOrThrow(requestInitiator.getId(), requestReceiver.getId());

        var friendRequest = new FriendRequestEntity();
        friendRequest.setInitiator(requestInitiator);
        friendRequest.setReceiver(requestReceiver);

        friendRequestRepository.save(friendRequest);
    }


    private void validateFriendRequestOrThrow(UUID requestInitiator, UUID requestReceiver) {
        if (requestInitiator.compareTo(requestReceiver) == 0)
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.FRIEND_REQUEST_SELF, "Cannot send friend request to yourself.");

        var friendRequestOptional = friendRequestRepository.findExistingRequestsBetweenUsers(requestInitiator, requestReceiver);
        if (friendRequestOptional.isPresent())
            throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.FRIEND_REQUEST_EXISTS, "Friend request already exists.");
    }


    @Transactional(readOnly = true)
    public List<PublicUserResponseDto> getPendingFriendRequests(Jwt jwt) {
        UserEntity userEntity = userService.getAuthenticatedEntityOrThrow(jwt);

        return friendRequestRepository.findInitiatorAsDtoByReceiver(userEntity.getId());
    }


    @Transactional(readOnly = true)
    public FriendRequestEntity getFriendRequestOrThrow(UserEntity requestInitiator, UserEntity requestReceiver) {
        return friendRequestRepository.findByInitiatorAndReceiver(requestInitiator, requestReceiver)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_REQUEST_NOT_FOUND, "Friend request doesn't exist"));
    }


    /**
     * removes any remaining friend requests on both sides
     */
    public void removeFriendRequestBetweenUsers(UUID userId, UUID toBeBlockedUserId) {
        friendRequestRepository.findExistingRequestsBetweenUsers(userId, toBeBlockedUserId)
                .ifPresent(friendRequestRepository::delete);
    }


    @Transactional
    public void deleteFriendRequestOrThrow(UserEntity requestCanceler, UserEntity requestRecipient) {
        FriendRequestEntity friendRequest = getFriendRequestOrThrow(requestCanceler, requestRecipient);

        friendRequestRepository.delete(friendRequest);
    }
}
