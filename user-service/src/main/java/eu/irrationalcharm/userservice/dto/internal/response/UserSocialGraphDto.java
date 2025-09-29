package eu.irrationalcharm.userservice.dto.internal.response;

import java.util.Set;

public record UserSocialGraphDto(
        String username,
        boolean isOnBoarded,
        Set<String> friendsUsername
) {
}
