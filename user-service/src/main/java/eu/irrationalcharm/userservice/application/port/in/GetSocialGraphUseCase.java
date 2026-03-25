package eu.irrationalcharm.userservice.application.port.in;

import eu.irrationalcharm.userservice.dto.internal.response.UserSocialGraphDto;

import java.util.UUID;

public interface GetSocialGraphUseCase {

    UserSocialGraphDto getSocialGraph(UUID userId);

    UserSocialGraphDto getSocialGraphByUsername(String username);
}
