package eu.irrationalcharm.userservice.controller;


import eu.irrationalcharm.dto.user_service.UserDto;
import eu.irrationalcharm.userservice.config.security.SecurityConfig;
import eu.irrationalcharm.userservice.dto.request.OnBoardingRequestDto;
import eu.irrationalcharm.userservice.enums.ErrorCode;
import eu.irrationalcharm.userservice.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.service.orchestrator.RegistrationOrchestrator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@WebMvcTest(RegistrationController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class RegistrationControllerWebLayerTest {

    @Autowired
    MockMvcTester mockMvc;

    @MockitoBean
    RegistrationOrchestrator registrationOrchestrator;

    @Autowired
    ObjectMapper objectMapper;

    @ParameterizedTest
    @CsvSource({"User_123, Paco",
            "AlphaFox, Kaylin",
            "GamerTag-99, Laura",
            "L33t_Hax0r, Natalia",
            "orl, aaa",
            "dataM1ner, qwertyuiopasdfghjkza",
            "PhoenixRisePhoenixRi, rrr_1"})
    @DisplayName("Test completeOnBoarding PATCH endpoint by validating username, it should return 201 ")
    void testCompleteOnBoarding_whenValidOnBoardingDetailsProvided_shouldReturn201(String username, String displayName) {
        // Arrange
        String phoneNumber = "666555444";
        String providerId = UUID.randomUUID().toString();
        String email = "example@gmail.com";

        var onBoardingRequest = new OnBoardingRequestDto(username, displayName, phoneNumber, "", "");
        var userDto = new UserDto(providerId, username, displayName, email, phoneNumber, "", "");

        when(registrationOrchestrator.onBoarding(eq(onBoardingRequest), any(Jwt.class))).thenReturn(userDto);


        // Act
        MvcTestResult mvcTestResult = mockMvc.post().uri("/api/register/onboard")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(onBoardingRequest))
                .with(jwt())
                .exchange();

        // Assert
        assertThat(mvcTestResult)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("code").isEqualTo(SuccessfulCode.USER_CREATED.toString());
                    assertThat(json).extractingPath("status").isEqualTo(HttpStatus.CREATED.value());
                    assertThat(json).extractingPath("data.providerId").isEqualTo(providerId);
                    assertThat(json).extractingPath("data.email").isEqualTo(email);
                    assertThat(json).extractingPath("data.displayName").isEqualTo(displayName);
                });
    }


    @ParameterizedTest
    @CsvSource({"Us, Paco",
            "Alpha`Fox, Kaylin",
            "GamerTag{99, Laura",
            "+_, Natalia",
            "+pp, aaa",
            "|sssakk, qwertyuiopasdfghjkza",
            "PhoeniPhoenixRisePhoenixRise, rrr_1"})
    @DisplayName("Test completeOnBoarding PATCH endpoint by validating username, it should return 400 ")
    void testCompleteOnBoarding_whenInvalidUsernameProvided_shouldReturn400(String username, String displayName) {
        // Arrange
        String phoneNumber = "666555444";
        String providerId = UUID.randomUUID().toString();
        String email = "example@gmail.com";

        var onBoardingRequest = new OnBoardingRequestDto(username, displayName, phoneNumber, "", "");
        var userDto = new UserDto(providerId, username, displayName, email, phoneNumber, "", "");

        when(registrationOrchestrator.onBoarding(eq(onBoardingRequest), any(Jwt.class))).thenReturn(userDto);


        // Act
        MvcTestResult mvcTestResult = mockMvc.post().uri("/api/register/onboard")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(onBoardingRequest))
                .with(jwt())
                .exchange();

        // Assert
        assertThat(mvcTestResult)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("code").isEqualTo(ErrorCode.VALIDATION_ERROR.toString());
                    assertThat(json).extractingPath("status").isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(json).extractingPath("error").isNotEmpty();
                });
    }


}


