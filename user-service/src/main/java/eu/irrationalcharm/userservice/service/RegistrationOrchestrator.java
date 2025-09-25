package eu.irrationalcharm.userservice.service;

import eu.irrationalcharm.userservice.constants.JwtClaims;
import eu.irrationalcharm.userservice.dto.request.OnBoardingRequestDto;
import eu.irrationalcharm.userservice.dto.UserDto;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.userservice.enums.ErrorCode;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.userservice.mapper.UserMapper;
import eu.irrationalcharm.userservice.repository.UserRepository;

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
    private final IdentityProviderService identityProviderService;
    private final UserValidatorService userValidatorService;


    @Transactional
    @PreAuthorize("isAuthenticated()")
    public UserDto onBoarding(OnBoardingRequestDto boardingDto, Jwt authJwt) {
        String email = authJwt.getClaimAsString(JwtClaims.EMAIL);

        switch ( userValidatorService.validateOnBoardingUser(boardingDto, email) ) {
            case USERNAME_TAKEN -> throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.USERNAME_ALREADY_EXISTS, String.format("Username %s is not available", boardingDto.username()));
            case EMAIL_TAKEN -> throw new BusinessException(HttpStatus.CONFLICT, ErrorCode.EMAIL_TAKEN, String.format("An account has already been created with: %s", email));
            case USER_AVAILABLE -> {
                //User validation passed
            }
        }

        var userDto = UserDto.builder()
                .username(boardingDto.username())
                .displayName(boardingDto.displayName())
                .email(email)
                .mobileNumber(boardingDto.mobileNumber())
                .profileBio(boardingDto.profileBio())
                .profileImageUrl(boardingDto.profileImageUrl())
                .build();

        UserEntity savedUser = userRepository.save(UserMapper.mapToUserEntity(userDto));

        identityProviderService.persistIdentityProvider(authJwt, savedUser);

        return UserMapper.mapToUserDto(savedUser);
    }
}
