package eu.irrationalcharm.userservice.service;

import eu.irrationalcharm.userservice.dto.request.UpdateFriendRequestDto;
import eu.irrationalcharm.userservice.dto.response.PublicUserResponseDto;
import eu.irrationalcharm.userservice.entity.FriendRequestEntity;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.userservice.enums.ErrorCode;
import eu.irrationalcharm.userservice.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.userservice.repository.FriendRequestRepository;
import eu.irrationalcharm.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class FriendRequestService {


    private final UserService userService;
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipService friendshipService;
    private final UserFriendshipServicePreferenceService friendPreferenceService;


    @Transactional
    public void sendFriendRequest(Jwt jwt, String username) {
        UserEntity requestReceiver = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.USERNAME_NOT_FOUND, String.format("User %s does not exist", username)));

        UserEntity requestInitiator = userService.getAuthenticatedEntityOrThrow(jwt);
        validateFriendRequestOrThrow(requestInitiator, requestReceiver);

        var friendRequest = new FriendRequestEntity();
        friendRequest.setInitiator(requestInitiator);
        friendRequest.setReceiver(requestReceiver);

        friendRequestRepository.save(friendRequest);
    }


    @Transactional(readOnly = true)
    public List<PublicUserResponseDto> getPendingFriendRequests(Jwt jwt) {
        UserEntity userEntity = userService.getAuthenticatedEntityOrThrow(jwt);

        return friendRequestRepository.findInitiatorAsDtoByReceiver(userEntity.getId());
    }


    @Transactional
    public SuccessfulCode updateFriendRequest(Jwt jwt, UpdateFriendRequestDto updateFriendRequestDto, String requestInitiatorUsername) {
        UserEntity requestInitiatorEntity = userRepository.findByUsername(requestInitiatorUsername)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.USERNAME_NOT_FOUND, String.format("User %s does not exist", requestInitiatorUsername)));

        UserEntity userEntity = userService.getAuthenticatedEntityOrThrow(jwt);

        FriendRequestEntity friendRequestEntity = friendRequestRepository.findByInitiatorAndReceiver(requestInitiatorEntity, userEntity)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.FRIEND_REQUEST_NOT_FOUND, "Friend request doesn't exist"));

        return switch (updateFriendRequestDto.action()) {
            case ACCEPT_REQUEST -> {
                friendshipService.addFriendOrThrow(requestInitiatorEntity.getId(), userEntity.getId());
                friendRequestRepository.delete(friendRequestEntity);
                yield SuccessfulCode.FRIEND_REQUEST_ACCEPTED;
            }
            case DECLINE_REQUEST -> {
                friendRequestRepository.delete(friendRequestEntity);
                yield SuccessfulCode.FRIEND_REQUEST_DECLINED;
            }
            case CANCEL_REQUEST -> {
                friendRequestRepository.delete(friendRequestEntity);
                yield SuccessfulCode.FRIEND_REQUEST_CANCELLED;
            }
        };

    }


    private void validateFriendRequestOrThrow(UserEntity requestInitiator, UserEntity requestReceiver) {
        if (requestInitiator.getId().compareTo(requestReceiver.getId()) == 0)
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.FRIEND_REQUEST_SELF, "Cannot send friend request to yourself.");

        if (friendPreferenceService.isBlocking(requestReceiver.getId(), requestInitiator.getId()))
            throw new BusinessException(HttpStatus.FORBIDDEN, ErrorCode.FRIEND_REQUEST_BLOCKED_BY_USER, "Cannot send friend request. The recipient has blocked you.");

        if (friendshipService.areFriends(requestInitiator.getId(), requestReceiver.getId()))
            throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.FRIEND_REQUEST_ALREADY_FRIENDS, "Cannot send friend request. The recipient is already your friend");

        var friendRequestOptional = friendRequestRepository.findExistingRequestsBetweenUsers(requestInitiator.getId(), requestReceiver.getId());
        if (friendRequestOptional.isPresent())
            throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.FRIEND_REQUEST_EXISTS, "Friend request already exists.");

    }

}
