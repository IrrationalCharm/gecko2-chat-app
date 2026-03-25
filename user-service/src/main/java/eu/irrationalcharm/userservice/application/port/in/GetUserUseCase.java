package eu.irrationalcharm.userservice.application.port.in;

import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.dto.user_service.UserDto;
import eu.irrationalcharm.userservice.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface GetUserUseCase {

    PublicUserResponseDto getPublicProfile(String username);

    Optional<UserDto> getAuthenticatedUserDto(UUID userId);

    User getUserOrThrow(UUID userId);

    User getUserByUsernameOrThrow(String username);
}
