package eu.irrationalcharm.userservice.application.port.in;

import eu.irrationalcharm.dto.user_service.UserDto;
import eu.irrationalcharm.userservice.dto.request.UpdateUserProfileRequestDto;

import java.util.UUID;

public interface UpdateUserUseCase {

    UserDto updateUserDetails(UUID userId, UpdateUserProfileRequestDto dto);

    void updateProfileImageUrl(UUID userId, String profileImageUrl);
}
