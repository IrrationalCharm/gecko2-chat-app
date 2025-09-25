package eu.irrationalcharm.userservice.service;

import eu.irrationalcharm.userservice.entity.UserFriendshipPreferenceEntity;
import eu.irrationalcharm.userservice.enums.ErrorCode;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.userservice.repository.UserFriendshipPreferenceRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
        if (isBlocking(userId, friendId) || isBlocking(friendId, userId))
            throw new BusinessException(HttpStatus.FORBIDDEN, ErrorCode.FRIEND_REQUEST_BLOCKED_BY_USER, "Cannot add friend");

        if ( userFriendshipPreferenceRepository.findByUserIdAndFriendId(userId, friendId).isEmpty() ) {
            var fpEntity = new UserFriendshipPreferenceEntity();
            fpEntity.setUserId(userId);
            fpEntity.setFriendId(friendId);

            userFriendshipPreferenceRepository.save(fpEntity);
        }
    }


    @Transactional(readOnly = true)
    public UserFriendshipPreferenceEntity getFriendshipPreferenceOrCreate(UUID userId, UUID friendId) {
        return userFriendshipPreferenceRepository.findByUserIdAndFriendId(userId, friendId)
                .orElseGet(() -> {
                    var fpEntity = new UserFriendshipPreferenceEntity();
                    fpEntity.setUserId(userId);
                    fpEntity.setFriendId(friendId);
                    return fpEntity;
                });
    }
}
