package eu.irrationalcharm.userservice.service;

import eu.irrationalcharm.userservice.dto.request.OnBoardingRequestDto;
import eu.irrationalcharm.userservice.enums.UserValidatorStatus;
import eu.irrationalcharm.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserValidatorService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserValidatorStatus validateOnBoardingUser(OnBoardingRequestDto onBoardingDto, String email) {
        if ( isUsernameTaken(onBoardingDto.username()) )
            return UserValidatorStatus.USERNAME_TAKEN;

        if ( isAccountWithEmailCreated(email) )
            return UserValidatorStatus.EMAIL_TAKEN;

        return UserValidatorStatus.USER_AVAILABLE;
    }

    public boolean isAccountWithEmailCreated(String email) {
        return userRepository.existsUserEntityByEmail(email);
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.existsUserEntityByUsername(username);
    }
}
