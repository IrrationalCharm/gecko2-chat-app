package eu.irrationalcharm.userservice.application.service;

import eu.irrationalcharm.enums.ErrorCode;
import eu.irrationalcharm.userservice.application.port.in.ManageFriendPreferenceUseCase;
import eu.irrationalcharm.userservice.application.port.out.FriendRequestRepositoryPort;
import eu.irrationalcharm.userservice.application.port.out.FriendshipPreferenceRepositoryPort;
import eu.irrationalcharm.userservice.application.port.out.FriendshipRepositoryPort;
import eu.irrationalcharm.userservice.application.port.out.UserRepositoryPort;
import eu.irrationalcharm.userservice.domain.model.FriendshipPreference;
import eu.irrationalcharm.userservice.domain.model.User;
import eu.irrationalcharm.userservice.dto.response.FriendPreferenceDto;
import eu.irrationalcharm.userservice.dto.response.PatchFriendPreferenceDto;
import eu.irrationalcharm.userservice.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class FriendPreferenceService implements ManageFriendPreferenceUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final FriendshipRepositoryPort friendshipRepositoryPort;
    private final FriendRequestRepositoryPort friendRequestRepositoryPort;
    private final FriendshipPreferenceRepositoryPort friendshipPreferenceRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public FriendPreferenceDto getFriendPreference(UUID principalId, String friendUsername) {
        User principal = getUserOrThrow(principalId);
        User friend = getUserByUsernameOrThrow(friendUsername);

        FriendshipPreference preference = friendshipPreferenceRepositoryPort
                .findByUserAndFriend(principal.id(), friend.id())
                .orElseGet(() -> {
                    log.debug("No existing friend preference found. Returning default for user {} regarding friend {}",
                            principal.id(), friend.id());
                    return new FriendshipPreference(null, principal.id(), friend.id(), false, false, false);
                });

        return new FriendPreferenceDto(
                friend.username(),
                preference.blocked(),
                preference.muted(),
                preference.pinned()
        );
    }

    @Override
    @Transactional
    public PatchFriendPreferenceDto updateFriendPreference(UUID principalId, String friendUsername, PatchFriendPreferenceDto dto) {
        User principal = getUserOrThrow(principalId);
        User friend = getUserByUsernameOrThrow(friendUsername);

        log.info("Starting process to update friend preferences for user {} regarding friend {}",
                principal.id(), friend.id());

        FriendshipPreference preference = friendshipPreferenceRepositoryPort
                .findByUserAndFriend(principal.id(), friend.id())
                .orElseGet(() -> new FriendshipPreference(null, principal.id(), friend.id(), false, false, false));

        if (dto.isBlocked() != null && dto.isBlocked() != preference.blocked()) {
            if (dto.isBlocked()) {
                log.info("User {} is blocking user {}. Severing friendship and requests.", principal.id(), friend.id());
                blockUser(principal.id(), friend.id());
            } else {
                log.info("User {} is unblocking user {}", principal.id(), friend.id());
            }
            preference = preference.withBlocked(dto.isBlocked());
        }

        if (dto.isMuted() != null && dto.isMuted() != preference.muted()) {
            preference = preference.withMuted(dto.isMuted());
        }

        if (dto.isPinned() != null && dto.isPinned() != preference.pinned()) {
            preference = preference.withPinned(dto.isPinned());
        }

        FriendshipPreference saved = friendshipPreferenceRepositoryPort.save(preference);

        log.info("Successfully updated friend preferences for user {} regarding friend {}", principal.id(), friend.id());

        return new PatchFriendPreferenceDto(saved.blocked(), saved.muted(), saved.pinned());
    }

    private void blockUser(UUID userId, UUID toBeBlockedId) {
        if (friendshipRepositoryPort.areFriends(userId, toBeBlockedId)) {
            log.debug("Users {} and {} are friends. Removing friendship due to block.", userId, toBeBlockedId);
            friendshipRepositoryPort.deleteFriendship(userId, toBeBlockedId);
        }

        log.debug("Removing any pending friend requests between user {} and {}", userId, toBeBlockedId);
        friendRequestRepositoryPort.findExistingRequest(userId, toBeBlockedId)
                .ifPresent(request -> friendRequestRepositoryPort.delete(request.id()));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepositoryPort.findById(userId).orElseThrow(() -> {
            log.warn("Could not find account for user {}", userId);
            return new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.ON_BOARDING_REQUIRED,
                    String.format("Could not find account with this user id: %s", userId));
        });
    }

    private User getUserByUsernameOrThrow(String username) {
        return userRepositoryPort.findByUsername(username).orElseThrow(() -> {
            log.warn("Could not find user with username: {}", username);
            return new BusinessException(HttpStatus.NOT_FOUND, ErrorCode.USERNAME_NOT_FOUND,
                    String.format("User with username %s not found.", username));
        });
    }
}
