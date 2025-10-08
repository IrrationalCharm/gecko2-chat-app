package eu.irrationalcharm.userservice.service;

import eu.irrationalcharm.userservice.constants.JwtClaims;
import eu.irrationalcharm.userservice.dto.request.OnBoardingRequestDto;
import eu.irrationalcharm.userservice.enums.UserValidatorStatus;
import eu.irrationalcharm.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserValidatorService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserValidatorStatus validateOnBoardingUser(OnBoardingRequestDto onBoardingDto, Jwt jwtAuth) {

        if ( isProviderIdAlreadyRegistered(jwtAuth.getClaimAsString(JwtClaims.SUBJECT)) )
            return UserValidatorStatus.PROVIDER_ID_ALREADY_REGISTERED;

        if ( isAccountWithEmailCreated(jwtAuth.getClaimAsString(JwtClaims.EMAIL)) )
            return UserValidatorStatus.EMAIL_TAKEN;

        if ( isUsernameTaken(onBoardingDto.username()) )
            return UserValidatorStatus.USERNAME_TAKEN;


        return UserValidatorStatus.USER_AVAILABLE;
    }

    private boolean isProviderIdAlreadyRegistered(String claimAsString) {
        return userRepository.existsUserEntityByProviderId(claimAsString);
    }

    private boolean isAccountWithEmailCreated(String email) {
        return userRepository.existsUserEntityByEmail(email);
    }

    private boolean isUsernameTaken(String username) {
        return userRepository.existsUserEntityByUsername(username);
    }
}
