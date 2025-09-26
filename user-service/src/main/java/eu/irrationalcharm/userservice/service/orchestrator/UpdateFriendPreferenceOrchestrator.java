package eu.irrationalcharm.userservice.service.orchestrator;

import eu.irrationalcharm.userservice.dto.response.FriendPreferenceDto;
import eu.irrationalcharm.userservice.dto.response.PatchFriendPreferenceDto;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.userservice.entity.UserFriendshipPreferenceEntity;
import eu.irrationalcharm.userservice.repository.UserFriendshipPreferenceRepository;
import eu.irrationalcharm.userservice.service.FriendRequestService;
import eu.irrationalcharm.userservice.service.FriendshipService;
import eu.irrationalcharm.userservice.service.UserFriendshipPreferenceService;
import eu.irrationalcharm.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UpdateFriendPreferenceOrchestrator {

    private final UserFriendshipPreferenceRepository userFriendshipPreferenceRepository;
    private final UserFriendshipPreferenceService friendPreferenceService;
    private final FriendshipService friendshipService;
    private final FriendRequestService friendRequestService;
    private final UserService userService;


    @Transactional
    public PatchFriendPreferenceDto updateFriendPreference(Jwt jwt, String friendUsername, PatchFriendPreferenceDto friendPreference) {
        UserEntity principalEntity = userService.getAuthenticatedEntityOrThrow(jwt);
        UserEntity friendEntity = userService.getEntityByUsernameOrThrow(friendUsername);

        var friendPreferenceEntity = friendPreferenceService.getFriendshipPreferenceOrCreate(principalEntity.getId(), friendEntity.getId());


        if (friendPreference.isBlocked() != null && friendPreference.isBlocked() != friendPreferenceEntity.isBlocked()) {
            if(friendPreference.isBlocked()) { //Block friend
                blockUser(principalEntity, friendEntity);
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


    private void blockUser(UserEntity userEntity, UserEntity toBeBlocked) {
        if( friendshipService.areFriends(userEntity, toBeBlocked) ) {
            friendshipService.removeFriend(userEntity, toBeBlocked);
        }

        friendRequestService.removeFriendRequestBetweenUsers(userEntity.getId(), toBeBlocked.getId());
    }


    @Transactional(readOnly = true)
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
}
