package eu.irrationalcharm.userservice.application.service;

import eu.irrationalcharm.userservice.application.port.out.UserRepositoryPort;
import eu.irrationalcharm.userservice.dto.request.OnBoardingRequestDto;
import eu.irrationalcharm.userservice.enums.UserValidatorStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserValidatorService {

    private final UserRepositoryPort userRepositoryPort;

    @Transactional(readOnly = true)
    public UserValidatorStatus validateOnBoarding(OnBoardingRequestDto dto, String providerId, String email) {
        if (userRepositoryPort.existsByProviderId(providerId))
            return UserValidatorStatus.PROVIDER_ID_ALREADY_REGISTERED;

        if (userRepositoryPort.existsByEmail(email))
            return UserValidatorStatus.EMAIL_TAKEN;

        if (userRepositoryPort.existsByUsername(dto.username()))
            return UserValidatorStatus.USERNAME_TAKEN;

        return UserValidatorStatus.USER_AVAILABLE;
    }

    @Transactional(readOnly = true)
    public boolean isUsernameTaken(String username) {
        return userRepositoryPort.existsByUsername(username);
    }
}
