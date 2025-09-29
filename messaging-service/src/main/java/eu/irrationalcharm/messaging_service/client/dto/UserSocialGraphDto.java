package eu.irrationalcharm.messaging_service.client.dto;

import java.util.Set;

public record UserSocialGraphDto(
        String username,
        boolean isOnBoarded,
        Set<String> friendsUsername
) {
}
