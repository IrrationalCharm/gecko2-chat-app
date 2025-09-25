package eu.irrationalcharm.userservice.service;

import eu.irrationalcharm.userservice.dto.response.PublicUserResponseDto;
import eu.irrationalcharm.userservice.entity.FriendshipEntity;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.userservice.enums.ErrorCode;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.userservice.repository.FriendshipRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;


    @Transactional(readOnly = true)
    public boolean areFriends(UserEntity userA, UserEntity userB) {
        FriendshipId orderedFriendshipIds = getOrderedFriendshipId(userA, userB);

        return friendshipRepository.existsByFriendAAndFriendB(orderedFriendshipIds.friendA, orderedFriendshipIds.friendB);
    }


    /**
     * Only needed on lookup, not persistence.
     * If two users are friends, and is persisted into FriendshipRepository, automatically it is determined which UUID
     * has the higher value and its stored accordingly. Look @PrePersist in FriendshipEntity.
     * On lookup, we just find the higher UUID and set it as the first parameter.
     */
    private FriendshipId getOrderedFriendshipId(UserEntity unorderedUserA, UserEntity unorderedUserB) {
        UUID userA_id = unorderedUserA.getId();
        UUID userB_id = unorderedUserB.getId();

        UserEntity orderedUserA = userA_id.compareTo(userB_id) > 0 ? unorderedUserA : unorderedUserB;
        UserEntity orderedUserB = userA_id.compareTo(userB_id) < 0 ? unorderedUserA : unorderedUserB;

        return new FriendshipId(orderedUserA, orderedUserB);
    }

    private record FriendshipId(UserEntity friendA, UserEntity friendB) {  }


    @Transactional(readOnly = true)
    public Set<PublicUserResponseDto> getFriends(UserEntity userEntity) {
        Set<UserEntity> userFriends = friendshipRepository.findAllFriendsByUserId(userEntity);

        if (userFriends.isEmpty())
            return Collections.emptySet();

        return userFriends.stream()
                .map(user -> PublicUserResponseDto.builder()
                        .displayName(user.getDisplayName())
                        .profileImageUrl(user.getProfileImageUrl())
                        .username(user.getUsername())
                        .profileBio(user.getProfileBio())
                        .build())
                .collect(Collectors.toSet());
    }


    @Transactional
    public boolean removeFriend(UserEntity userA, UserEntity userB) {
        FriendshipId friendshipId = getOrderedFriendshipId(userA, userB);

        Optional<FriendshipEntity> friendship = friendshipRepository.findByFriendAAndFriendB(friendshipId.friendA, friendshipId.friendB);

        if( friendship.isPresent() ) {
            friendshipRepository.delete(friendship.get());
            return true;
        }

        return false;
    }


    @Transactional
    public void addFriendOrThrow(UserEntity userA, UserEntity userB) {
        validateFriendshipOrThrow(userA, userB);

        var newFriendship = new FriendshipEntity();
        newFriendship.setFriendA(userA);
        newFriendship.setFriendB(userB);

        friendshipRepository.save(newFriendship);
    }


    @SuppressWarnings("SpringTransactionalMethodCallsInspection")
    private void validateFriendshipOrThrow(UserEntity userA, UserEntity userB) {
        if (userA.getId().compareTo(userB.getId()) == 0)
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.FRIEND_REQUEST_SELF, "Cannot add yourself as friend.");

        if (areFriends(userA, userB))
            throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.FRIEND_REQUEST_ALREADY_FRIENDS, "Cannot add friend. Users are already friends");
    }
}
