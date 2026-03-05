package eu.irrationalcharm.userservice.service;

import eu.irrationalcharm.userservice.entity.UserFriendshipPreferenceEntity;
import eu.irrationalcharm.enums.ErrorCode;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.userservice.repository.UserFriendshipPreferenceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserFriendshipPreferenceService {

    private final UserFriendshipPreferenceRepository userFriendshipPreferenceRepository;


    public boolean isBlocking(UUID potentialBlockerId, UUID potentialBlockedId) {
        var potencialBlockerFriendPreferenceOptional = userFriendshipPreferenceRepository.findByUserIdAndFriendId(potentialBlockerId, potentialBlockedId);

        return potencialBlockerFriendPreferenceOptional.map(UserFriendshipPreferenceEntity::isBlocked).orElse(false);

    }


    @Transactional
    public void createNewFriendshipPreferenceEntityOrThrow(UUID userId, UUID friendId) {
        if (isBlocking(userId, friendId) || isBlocking(friendId, userId)) {
            log.warn("Cannot create friendship preference: A block exists between user {} and user {}", userId, friendId);
            throw new BusinessException(HttpStatus.FORBIDDEN, ErrorCode.FRIEND_REQUEST_BLOCKED_BY_USER, "Cannot add friend");
        }

        if ( userFriendshipPreferenceRepository.findByUserIdAndFriendId(userId, friendId).isEmpty() ) {
            log.debug("No existing friendship preference found. Creating default preference for user {} regarding friend {}", userId, friendId);

            var fpEntity = new UserFriendshipPreferenceEntity();
            fpEntity.setUserId(userId);
            fpEntity.setFriendId(friendId);

            userFriendshipPreferenceRepository.save(fpEntity);
        }
    }


    /**
     *
     * @return returns a managed entity if found in the DB, if not, it creates on, persists it and also returns a managed entity
     */
    @Transactional
    public UserFriendshipPreferenceEntity getOrCreateFriendshipPreference(UUID userId, UUID friendId) {
        return userFriendshipPreferenceRepository.findByUserIdAndFriendId(userId, friendId)
                .orElseGet(() -> {
                    log.debug("Friendship preference not found. Creating and persisting new default preference for user {} regarding friend {}", userId, friendId);

                    var fpEntity = new UserFriendshipPreferenceEntity();
                    fpEntity.setUserId(userId);
                    fpEntity.setFriendId(friendId);
                    return userFriendshipPreferenceRepository.save(fpEntity);
                });
    }
}
