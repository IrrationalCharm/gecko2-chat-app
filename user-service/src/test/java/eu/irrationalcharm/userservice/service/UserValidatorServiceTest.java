package eu.irrationalcharm.userservice.service;

import eu.irrationalcharm.userservice.constants.JwtClaims;
import eu.irrationalcharm.userservice.dto.request.OnBoardingRequestDto;
import eu.irrationalcharm.userservice.enums.UserValidatorStatus;
import eu.irrationalcharm.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorServiceTest {

    @InjectMocks
    private UserValidatorService userValidatorService;

    @Mock
    private UserRepository userRepository;

    private String username;
    private Jwt jwt;
    private OnBoardingRequestDto onBoardingRequest;

    @BeforeEach
    void setup(){
        username = "testUser";
        jwt = Jwt.withTokenValue("token")
                .header("typ","JWT")
                .claim(JwtClaims.EMAIL, "test@gmail.com")
                .claim(JwtClaims.SUBJECT, "123456789")
                .build();

        onBoardingRequest = OnBoardingRequestDto.builder()
                .username(username)
                .build();
    }

    @Test
    @DisplayName("Testing if validateOnBoardingUser returns USER_AVAILABLE")
    void testValidateOnBoardingUser_whenOnBoardingDtoProvided_returnsUserAvailable() {
        // Arrange
        when(userRepository.existsUserEntityByProviderId(eq(jwt.getClaimAsString(JwtClaims.SUBJECT))))
                .thenReturn(false);
        when(userRepository.existsUserEntityByEmail(eq(jwt.getClaimAsString(JwtClaims.EMAIL))))
                .thenReturn(false);
        when(userRepository.existsUserEntityByUsername(eq(username)))
                .thenReturn(false);


        // Act
        UserValidatorStatus status = userValidatorService.validateOnBoardingUser(onBoardingRequest, jwt);

        // Assert
        assertEquals(UserValidatorStatus.USER_AVAILABLE, status, () -> "Expected status USER_AVAILABLE but got " + status);
        verify(userRepository, times(1)).existsUserEntityByProviderId(eq(jwt.getClaimAsString(JwtClaims.SUBJECT)));
        verify(userRepository, times(1)).existsUserEntityByUsername(eq(username));
        verify(userRepository, times(1)).existsUserEntityByEmail(eq(jwt.getClaimAsString(JwtClaims.EMAIL)));
    }


    @Test
    @DisplayName("Testing if validateOnBoardingUser returns USERNAME_TAKEN")
    void testValidateOnBoardingUser_whenUsernameAlreadyCreated_returnsUsernameTaken() {
        // Arrange
        when(userRepository.existsUserEntityByProviderId(eq(jwt.getClaimAsString(JwtClaims.SUBJECT))))
                .thenReturn(false);
        when(userRepository.existsUserEntityByEmail(eq(jwt.getClaimAsString(JwtClaims.EMAIL))))
                .thenReturn(false);
        when(userRepository.existsUserEntityByUsername(eq(username)))
                .thenReturn(true);


        // Act
        UserValidatorStatus status = userValidatorService.validateOnBoardingUser(onBoardingRequest, jwt);

        // Assert
        assertEquals(UserValidatorStatus.USERNAME_TAKEN, status, () -> "Expected status USERNAME_TAKEN but got " + status);
        verify(userRepository, times(1)).existsUserEntityByProviderId(eq(jwt.getClaimAsString(JwtClaims.SUBJECT)));
        verify(userRepository, times(1)).existsUserEntityByUsername(eq(username));
        verify(userRepository, times(1)).existsUserEntityByEmail(eq(jwt.getClaimAsString(JwtClaims.EMAIL)));
    }


    @Test
    @DisplayName("Testing if validateOnBoardingUser returns EMAIL_TAKEN")
    void testValidateOnBoardingUser_whenEmailAlreadyCreated_returnsEmailTaken() {
        // Arrange
        when(userRepository.existsUserEntityByProviderId(eq(jwt.getClaimAsString(JwtClaims.SUBJECT))))
                .thenReturn(false);
        when(userRepository.existsUserEntityByEmail(eq(jwt.getClaimAsString(JwtClaims.EMAIL))))
                .thenReturn(true);

        // Act
        UserValidatorStatus status = userValidatorService.validateOnBoardingUser(onBoardingRequest, jwt);

        // Assert
        assertEquals(UserValidatorStatus.EMAIL_TAKEN, status, () -> "Expected status EMAIL_TAKEN but got " + status);
        verify(userRepository, times(1)).existsUserEntityByProviderId(eq(jwt.getClaimAsString(JwtClaims.SUBJECT)));
        verify(userRepository, never()).existsUserEntityByUsername(any());
        verify(userRepository, times(1)).existsUserEntityByEmail(jwt.getClaimAsString(JwtClaims.EMAIL));
    }


    @Test
    @DisplayName("Testing if validateOnBoardingUser returns PROVIDER_ID_ALREADY_REGISTERED")
    void testValidateOnBoardingUser_whenProviderIdAlreadyExists_returnsProviderIdAlreadyExists() {
        // Arrange
        when(userRepository.existsUserEntityByProviderId(eq(jwt.getClaimAsString(JwtClaims.SUBJECT))))
                .thenReturn(true);

        // Act
        UserValidatorStatus status = userValidatorService.validateOnBoardingUser(onBoardingRequest, jwt);

        // Assert
        assertEquals(UserValidatorStatus.PROVIDER_ID_ALREADY_REGISTERED, status, () -> "Expected status PROVIDER_ID_ALREADY_REGISTERED but got " + status);
        verify(userRepository, times(1)).existsUserEntityByProviderId(eq(jwt.getClaimAsString(JwtClaims.SUBJECT)));
        verify(userRepository, never()).existsUserEntityByUsername(any());
        verify(userRepository, never()).existsUserEntityByEmail(any());
    }

}
