package eu.irrationalcharm.userservice.application.service;

import eu.irrationalcharm.userservice.application.port.in.GetSocialGraphUseCase;
import eu.irrationalcharm.userservice.application.port.out.FriendshipRepositoryPort;
import eu.irrationalcharm.userservice.application.port.out.UserRepositoryPort;
import eu.irrationalcharm.userservice.domain.model.User;
import eu.irrationalcharm.userservice.dto.internal.response.UserSocialGraphDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialGraphService implements GetSocialGraphUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final FriendshipRepositoryPort friendshipRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public UserSocialGraphDto getSocialGraph(UUID userId) {
        if (userId == null) {
            log.debug("No userId provided. Returning empty social graph (isOnBoarded = false).");
            return new UserSocialGraphDto(null, false, null);
        }

        Optional<User> userOptional = userRepositoryPort.findById(userId);
        return buildSocialGraphDto(userOptional);
    }

    @Override
    @Transactional(readOnly = true)
    public UserSocialGraphDto getSocialGraphByUsername(String username) {
        Optional<User> userOptional = userRepositoryPort.findByUsername(username);
        return buildSocialGraphDto(userOptional);
    }

    private UserSocialGraphDto buildSocialGraphDto(Optional<User> userOptional) {
        if (userOptional.isEmpty()) {
            log.debug("User not found. Returning empty social graph (isOnBoarded = false).");
            return new UserSocialGraphDto(null, false, null);
        }

        User user = userOptional.get();
        Set<String> friendIds = friendshipRepositoryPort.findFriendIds(user.id())
                .stream()
                .map(UUID::toString)
                .collect(Collectors.toSet());

        log.debug("Built social graph for user {}. Total friends: {}", user.id(), friendIds.size());
        return new UserSocialGraphDto(user.id().toString(), true, friendIds);
    }
}
