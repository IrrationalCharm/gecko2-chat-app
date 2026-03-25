package eu.irrationalcharm.userservice.application.port.in;

import eu.irrationalcharm.dto.user_service.UserDto;
import eu.irrationalcharm.userservice.dto.request.OnBoardingRequestDto;

public interface RegisterUserUseCase {

    UserDto onBoard(String providerId, String email, OnBoardingRequestDto dto);

    boolean checkUsernameAvailability(String username);
}
