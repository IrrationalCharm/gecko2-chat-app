package eu.irrationalcharm.userservice.service.orchestrator;

import eu.irrationalcharm.dto.user_service.UserDto;
import eu.irrationalcharm.userservice.client.KeycloakAdminClient;
import eu.irrationalcharm.userservice.config.properties.CdnProperties;
import eu.irrationalcharm.userservice.constants.JwtClaims;
import eu.irrationalcharm.userservice.dto.request.OnBoardingRequestDto;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.enums.ErrorCode;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.userservice.mapper.UserMapper;
import eu.irrationalcharm.userservice.repository.UserRepository;
import eu.irrationalcharm.userservice.service.UserValidatorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class RegistrationOrchestrator {

    private final UserRepository userRepository;
    private final UserValidatorService userValidatorService;
    private final KeycloakAdminClient keycloakAdminClient;
    private final CdnProperties cdnProperties;

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public UserDto onBoarding(OnBoardingRequestDto boardingDto, Jwt authJwt) {
        final String providerId = authJwt.getClaimAsString(JwtClaims.SUBJECT);
        final String email = authJwt.getClaimAsString(JwtClaims.EMAIL);

        log.info("Starting onboarding process for providerId: {} with requested username: {}", providerId, boardingDto.username());

        switch ( userValidatorService.validateOnBoardingUser(boardingDto, authJwt) ) {
            case PROVIDER_ID_ALREADY_REGISTERED -> {
                log.warn("Unboarding failed: ProviderId {} is already registered", providerId);
                throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.PROVIDER_ID_ALREADY_EXISTS, String.format("ProviderId %s is already registered", providerId));
            }
            case USERNAME_TAKEN -> {
                log.warn("Unboarding failed: Username {} is taken", boardingDto.username());
                throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.USERNAME_ALREADY_EXISTS, String.format("Username %s is not available", boardingDto.username()));
            }
            case EMAIL_TAKEN -> {
                log.warn("Unboarding failed: Email {} is already registered", email);
                throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.EMAIL_TAKEN, String.format("An account has already been created with: %s", email));
            }
            case USER_AVAILABLE -> log.debug("User validation passed for username: {}", boardingDto.username());

        }

        var userDto = UserDto.builder()
                .username(boardingDto.username())
                .providerId(providerId)
                .displayName(boardingDto.displayName())
                .email(email)
                .mobileNumber(boardingDto.mobileNumber())
                .profileBio(boardingDto.profileBio())
                .profileImageUrl(boardingDto.profileImageUrl())
                .build();

        UserEntity savedUser = userRepository.save(UserMapper.mapToUserEntity(userDto));
        log.debug("User entity saved to database with internal ID: {}", savedUser.getId());

        keycloakAdminClient.addUserAttributes(providerId, savedUser);

        log.info("Successfully completed onboarding for user: {}", savedUser.getUsername());
        return UserMapper.mapToUserDto(savedUser, cdnProperties.baseUrl());
    }

    public boolean checkUsernameAvailability(String username) {
        return userValidatorService.isUsernameTaken(username);
    }
}
