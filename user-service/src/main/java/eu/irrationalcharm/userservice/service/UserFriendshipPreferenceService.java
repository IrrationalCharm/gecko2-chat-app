package eu.irrationalcharm.userservice.service;

import eu.irrationalcharm.userservice.dto.response.FriendPreferenceDto;
import eu.irrationalcharm.userservice.dto.response.PatchFriendPreferenceDto;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.userservice.entity.UserFriendshipPreferenceEntity;
import eu.irrationalcharm.userservice.enums.ErrorCode;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.userservice.repository.UserFriendshipPreferenceRepository;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserFriendshipPreferenceService {

    private final UserFriendshipPreferenceRepository userFriendshipPreferenceRepository;
    private final FriendshipService friendshipService;
    private final FriendRequestService friendRequestService;

    private final UserService userService;

    public boolean isBlocking(UUID potentialBlockerId, UUID potentialBlockedId) {
        var potencialBlockerFriendPreferenceOptional = userFriendshipPreferenceRepository.findByUserIdAndFriendId(potentialBlockerId, potentialBlockedId);

        return potencialBlockerFriendPreferenceOptional.map(UserFriendshipPreferenceEntity::isBlocked).orElse(false);

    }


    @Transactional
    public void createNewFriendshipPreferenceEntity(UUID userId, UUID friendId) {
        if ( userFriendshipPreferenceRepository.findByUserIdAndFriendId(userId, friendId).isEmpty() ) {
            var fpEntity = new UserFriendshipPreferenceEntity();
            fpEntity.setUserId(userId);
            fpEntity.setFriendId(friendId);

            userFriendshipPreferenceRepository.save(fpEntity);
        }
    }


    public FriendPreferenceDto getFriendPreferenceOrThrow(Jwt jwt, String friendUsername) {
        UserEntity principalEntity = userService.getAuthenticatedEntityOrThrow(jwt);
        UserEntity friendEntity = userService.getEntityByUsernameOrThrow(friendUsername);

        UserFriendshipPreferenceEntity friendPreferenceEntity = userFriendshipPreferenceRepository.findByUserIdAndFriendId(principalEntity.getId(), friendEntity.getId())
                .orElseGet(() -> {
                    var newFriendPrefE = new UserFriendshipPreferenceEntity();
                    newFriendPrefE.setUserId(principalEntity.getId());
                    newFriendPrefE.setFriendId(friendEntity.getId());
                    return newFriendPrefE;
                });

        return new FriendPreferenceDto(
                friendEntity.getUsername(),
                friendPreferenceEntity.isBlocked(),
                friendPreferenceEntity.isMuted(),
                friendPreferenceEntity.isPinned());
    }


    public UserFriendshipPreferenceEntity getFriendPreferenceEntityOrThrow(UUID userId, UUID friendId) {

        return userFriendshipPreferenceRepository.findByUserIdAndFriendId(userId, friendId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.FRIEND_PREFERENCE_NOT_FOUND,
                        "No friend preference found for user"));
    }


    @Transactional
    public PatchFriendPreferenceDto updateFriendPreference(Jwt jwt, String friendUsername, PatchFriendPreferenceDto friendPreference) {
        UserEntity principalEntity = userService.getAuthenticatedEntityOrThrow(jwt);
        UserEntity friendEntity = userService.getEntityByUsernameOrThrow(friendUsername);

        var friendPreferenceEntity = userFriendshipPreferenceRepository.findByUserIdAndFriendId(principalEntity.getId(), friendEntity.getId())
                .orElseGet(() -> {
                    var newFriendPrefE = new UserFriendshipPreferenceEntity();
                    newFriendPrefE.setUserId(principalEntity.getId());
                    newFriendPrefE.setFriendId(friendEntity.getId());
                    return newFriendPrefE;
                });


        if (friendPreference.isBlocked() != null && friendPreference.isBlocked() != friendPreferenceEntity.isBlocked()) {
            if(friendPreference.isBlocked()) {
                blockUser(principalEntity.getId(), friendEntity.getId());
                friendPreferenceEntity.setBlocked(true);
            } else
                friendPreferenceEntity.setBlocked(false);
        }

        if (friendPreference.isMuted() != null && friendPreference.isMuted() != friendPreferenceEntity.isMuted()) {
            friendPreferenceEntity.setMuted(friendPreference.isMuted());
        }

        if (friendPreference.isPinned() != null && friendPreference.isPinned() != friendPreferenceEntity.isPinned()) {
            friendPreferenceEntity.setPinned(friendPreference.isPinned());
        }

        var userFP = userFriendshipPreferenceRepository.save(friendPreferenceEntity);
        return new PatchFriendPreferenceDto(
                userFP.isBlocked(),
                userFP.isMuted(),
                userFP.isPinned()
        );

    }


    private void blockUser(UUID userId, UUID toBeBlockedUserId) {
        //TODO friendshipService.removeFriend(userId, toBeBlockedUserId);
        friendRequestService.removeFriendRequestBetweenUsers(userId, toBeBlockedUserId);
    }




}
