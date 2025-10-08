package eu.irrationalcharm.userservice.service.orchestrator;

import eu.irrationalcharm.userservice.client.KeycloakAdminClient;
import eu.irrationalcharm.userservice.constants.JwtClaims;
import eu.irrationalcharm.userservice.dto.request.OnBoardingRequestDto;
import eu.irrationalcharm.userservice.dto.UserDto;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.userservice.enums.ErrorCode;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.userservice.mapper.UserMapper;
import eu.irrationalcharm.userservice.repository.UserRepository;
import eu.irrationalcharm.userservice.service.UserValidatorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class RegistrationOrchestrator {

    private final UserRepository userRepository;
    private final UserValidatorService userValidatorService;
    private final KeycloakAdminClient keycloakAdminClient;


    @Transactional
    @PreAuthorize("isAuthenticated()")
    public UserDto onBoarding(OnBoardingRequestDto boardingDto, Jwt authJwt) {
        final String providerId = authJwt.getClaimAsString(JwtClaims.SUBJECT);
        final String email = authJwt.getClaimAsString(JwtClaims.EMAIL);

        switch ( userValidatorService.validateOnBoardingUser(boardingDto, authJwt) ) {
            case PROVIDER_ID_ALREADY_REGISTERED -> throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.PROVIDER_ID_ALREADY_EXISTS, String.format("ProviderId %s is already registered", providerId));
            case USERNAME_TAKEN -> throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.USERNAME_ALREADY_EXISTS, String.format("Username %s is not available", boardingDto.username()));
            case EMAIL_TAKEN -> throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.EMAIL_TAKEN, String.format("An account has already been created with: %s", email));
            case USER_AVAILABLE -> {
                //User validation passed
            }
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
        keycloakAdminClient.addUserAttributes(providerId, savedUser);

        return UserMapper.mapToUserDto(savedUser);
    }
}
