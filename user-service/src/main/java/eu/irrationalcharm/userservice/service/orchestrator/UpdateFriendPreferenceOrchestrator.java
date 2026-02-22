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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

        log.info("Starting process to update friend preferences for user {} regarding friend {}", principalEntity.getId(), friendEntity.getId());

        var friendPreferenceEntity = friendPreferenceService.getOrCreateFriendshipPreference(principalEntity.getId(), friendEntity.getId());

        if (friendPreference.isBlocked() != null && friendPreference.isBlocked() != friendPreferenceEntity.isBlocked()) {
            if(friendPreference.isBlocked()) { //Block friend
                log.info("User {} is blocking user {}. Proceeding to sever friendship and requests.", principalEntity.getId(), friendEntity.getId());
                blockUser(principalEntity, friendEntity);
                friendPreferenceEntity.setBlocked(true);
            } else {
                log.info("User {} is unblocking user {}", principalEntity.getId(), friendEntity.getId());
                friendPreferenceEntity.setBlocked(false);
            }

        }

        if (friendPreference.isMuted() != null && friendPreference.isMuted() != friendPreferenceEntity.isMuted()) {
            log.debug("Updating mute status to {} for user {} regarding friend {}", friendPreference.isMuted(), principalEntity.getId(), friendEntity.getId());
            friendPreferenceEntity.setMuted(friendPreference.isMuted());
        }

        if (friendPreference.isPinned() != null && friendPreference.isPinned() != friendPreferenceEntity.isPinned()) {
            log.debug("Updating pin status to {} for user {} regarding friend {}", friendPreference.isPinned(), principalEntity.getId(), friendEntity.getId());
            friendPreferenceEntity.setPinned(friendPreference.isPinned());
        }

        var userFP = userFriendshipPreferenceRepository.save(friendPreferenceEntity);

        log.info("Successfully updated friend preferences for user {} regarding friend {}", principalEntity.getId(), friendEntity.getId());

        return new PatchFriendPreferenceDto(
                userFP.isBlocked(),
                userFP.isMuted(),
                userFP.isPinned()
        );

    }


    private void blockUser(UserEntity userEntity, UserEntity toBeBlocked) {
        if( friendshipService.areFriends(userEntity, toBeBlocked) ) {
            log.debug("Users {} and {} are currently friends. Removing friendship due to block.", userEntity.getId(), toBeBlocked.getId());
            friendshipService.removeFriend(userEntity, toBeBlocked);
        }

        log.debug("Removing any pending friend requests between user {} and {}", userEntity.getId(), toBeBlocked.getId());
        friendRequestService.removeFriendRequestBetweenUsers(userEntity.getId(), toBeBlocked.getId());
    }

    @Transactional(readOnly = true)
    public FriendPreferenceDto getFriendPreferenceOrThrow(Jwt jwt, String friendUsername) {
        UserEntity principalEntity = userService.getAuthenticatedEntityOrThrow(jwt);
        UserEntity friendEntity = userService.getEntityByUsernameOrThrow(friendUsername);

        UserFriendshipPreferenceEntity friendPreferenceEntity = userFriendshipPreferenceRepository.findByUserIdAndFriendId(principalEntity.getId(), friendEntity.getId())
                .orElseGet(() -> {
                    log.debug("No existing friend preference found between user {} and friend {}. Returning a default profile.", principalEntity.getId(), friendEntity.getId());
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
