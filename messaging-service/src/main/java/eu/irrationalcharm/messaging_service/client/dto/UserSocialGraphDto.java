package eu.irrationalcharm.messaging_service.client.dto;

import java.util.Set;

public record UserSocialGraphDto(
        String internalId,
        boolean isOnBoarded,
        Set<String> friendsInternalId
) {
}
