package eu.irrationalcharm.userservice.service;

import eu.irrationalcharm.userservice.dto.response.PublicUserResponseDto;
import eu.irrationalcharm.userservice.entity.FriendshipEntity;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.userservice.enums.ErrorCode;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.userservice.repository.FriendshipRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserFriendshipPreferenceService friendPreferenceService;
    private final UserService userService;

    /**
     *If two users are friends, and is persisted into FriendshipRepository, automatically it is determined which UUID
     *has the higher value and its stored accordingly. On lookup, we just find the higher UUID and set it as the first parameter.
     */
    public boolean areFriends(UUID userA, UUID userB) {
        UUID friendA = userA.compareTo(userB) > 0 ? userA : userB;
        UUID friendB = userA.compareTo(userB) < 0 ? userA : userB;

        return friendshipRepository.findByFriendAAndFriendB(friendA, friendB).isPresent();
    }


    @Transactional(readOnly = true)
    public List<PublicUserResponseDto> getFriends(Jwt jwt) {
        UserEntity userEntity = userService.getAuthenticatedEntityOrThrow(jwt);
        List<UUID> userFriends = friendshipRepository.findAllFriendsByUserId(userEntity.getId());

        if (userFriends.isEmpty())
            return Collections.emptyList();

        return userService.findAllUsersByUserIdAsDto(userFriends);
    }


    public void addFriendOrThrow(UUID userA, UUID userB) {
        validateFriendshipOrThrow(userA, userB);

        var newFriendship = new FriendshipEntity();
        newFriendship.setFriendA(userA);
        newFriendship.setFriendB(userB);

        friendshipRepository.save(newFriendship);
    }


    private void removeFriend(UUID userA, UUID userB) {
        if (areFriends(userA, userB)) {
            UUID friendA = userA.compareTo(userB) > 0 ? userA : userB;
            UUID friendB = userA.compareTo(userB) < 0 ? userA : userB;

            friendshipRepository.deleteByFriendAAndFriendB(friendA, friendB);
        }
    }


    private void validateFriendshipOrThrow(UUID userA, UUID userB) {
        if (userA.compareTo(userB) == 0)
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.FRIEND_REQUEST_SELF, "Cannot add yourself as friend.");

        if (friendPreferenceService.isBlocking(userA, userB) || friendPreferenceService.isBlocking(userB, userA))
            throw new BusinessException(HttpStatus.FORBIDDEN, ErrorCode.FRIEND_REQUEST_BLOCKED_BY_USER, "Cannot add friend");

        if (areFriends(userA, userB))
            throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.FRIEND_REQUEST_ALREADY_FRIENDS, "Cannot add friend. Users are already friends");
    }


}
