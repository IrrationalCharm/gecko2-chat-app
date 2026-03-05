package eu.irrationalcharm.userservice.dto.internal.response;

import java.util.Set;

public record UserSocialGraphDto(
        String internalId,
        boolean isOnBoarded,
        Set<String> friendsInternalId
) {
}
