package eu.irrationalcharm.userservice.application.service;

import eu.irrationalcharm.dto.user_service.UserDto;
import eu.irrationalcharm.enums.ErrorCode;
import eu.irrationalcharm.userservice.application.port.in.RegisterUserUseCase;
import eu.irrationalcharm.userservice.application.port.out.IdentityProviderPort;
import eu.irrationalcharm.userservice.application.port.out.UserRepositoryPort;
import eu.irrationalcharm.userservice.config.properties.CdnProperties;
import eu.irrationalcharm.userservice.domain.model.User;
import eu.irrationalcharm.userservice.dto.request.OnBoardingRequestDto;
import eu.irrationalcharm.userservice.enums.UserValidatorStatus;
import eu.irrationalcharm.userservice.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class RegistrationService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserValidatorService userValidatorService;
    private final IdentityProviderPort identityProviderPort;
    private final CdnProperties cdnProperties;

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public UserDto onBoard(String providerId, String email, OnBoardingRequestDto dto) {
        log.info("Starting onboarding process for providerId: {} with requested username: {}", providerId, dto.username());

        switch (userValidatorService.validateOnBoarding(dto, providerId, email)) {
            case PROVIDER_ID_ALREADY_REGISTERED -> {
                log.warn("Onboarding failed: ProviderId {} is already registered", providerId);
                throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.PROVIDER_ID_ALREADY_EXISTS,
                        String.format("ProviderId %s is already registered", providerId));
            }
            case USERNAME_TAKEN -> {
                log.warn("Onboarding failed: Username {} is taken", dto.username());
                throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.USERNAME_ALREADY_EXISTS,
                        String.format("Username %s is not available", dto.username()));
            }
            case EMAIL_TAKEN -> {
                log.warn("Onboarding failed: Email {} is already registered", email);
                throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.EMAIL_TAKEN,
                        String.format("An account has already been created with: %s", email));
            }
            case USER_AVAILABLE -> log.debug("User validation passed for username: {}", dto.username());
        }

        User newUser = new User(null, providerId, dto.username(), dto.displayName(),
                email, dto.mobileNumber(), dto.profileBio(), dto.profileImageUrl());

        User savedUser = userRepositoryPort.save(newUser);
        log.debug("User entity saved to database with internal ID: {}", savedUser.id());

        identityProviderPort.addUserAttributes(providerId, savedUser.id(), savedUser.username(), savedUser.email());

        log.info("Successfully completed onboarding for user: {}", savedUser.username());
        return UserService.mapToUserDto(savedUser, cdnProperties.baseUrl());
    }

    @Override
    public boolean checkUsernameAvailability(String username) {
        return userValidatorService.isUsernameTaken(username);
    }
}
