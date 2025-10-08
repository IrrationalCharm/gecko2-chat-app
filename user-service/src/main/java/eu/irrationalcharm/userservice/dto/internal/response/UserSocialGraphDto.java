package eu.irrationalcharm.userservice.dto.internal.response;

import java.util.Set;

public record UserSocialGraphDto(
        String userId,
        boolean isOnBoarded,
        Set<String> friendsId
) {
}
