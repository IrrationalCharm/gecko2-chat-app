package eu.irrationalcharm.userservice.service.orchestrator;

import eu.irrationalcharm.userservice.dto.internal.response.UserSocialGraphDto;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.userservice.service.FriendshipService;
import eu.irrationalcharm.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternalSocialGraphOrchestrator {

    private final FriendshipService friendshipService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public UserSocialGraphDto getSocialGraph(Jwt user) {
        Optional<UserEntity> userOptional = userService.getAuthenticatedEntity(user);

        return getUserSocialGraphDto(userOptional);
    }


    @Transactional(readOnly = true)
    public UserSocialGraphDto getSocialGraphByUsername(String username) {
        Optional<UserEntity> userOptional = userService.getEntityByUsername(username);

        return getUserSocialGraphDto(userOptional);
    }

    @NotNull
    private UserSocialGraphDto getUserSocialGraphDto(Optional<UserEntity> userOptional) {
        if(userOptional.isEmpty()) {
            return new UserSocialGraphDto(null, false, null);
        }

        UserEntity userEntity = userOptional.get();
        Set<String> usersFriends = friendshipService.getFriendsId(userEntity).stream()
                .map(UUID::toString)
                .collect(Collectors.toSet());

        return new UserSocialGraphDto(userEntity.getId().toString(), true, usersFriends);
    }
}
