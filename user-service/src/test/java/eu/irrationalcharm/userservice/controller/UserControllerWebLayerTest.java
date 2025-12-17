package eu.irrationalcharm.userservice.controller;



import eu.irrationalcharm.userservice.config.security.SecurityConfig;
import eu.irrationalcharm.userservice.dto.UserDto;
import eu.irrationalcharm.userservice.dto.request.UpdateUserProfileRequestDto;
import eu.irrationalcharm.userservice.dto.response.PublicUserResponseDto;
import eu.irrationalcharm.userservice.enums.ErrorCode;
import eu.irrationalcharm.userservice.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

@WebMvcTest({UserController.class})
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class UserControllerWebLayerTest {

    private static final String baseURI = "/api/v1/users";

    @Autowired
    MockMvcTester mockMvcTester;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    @ParameterizedTest
    @CsvSource({"User_123",
            "AlphaFox",
            "GamerTag-99",
            "L33t_Hax0r",
            "orl",
            "dataM1ner",
            "PhoenixRisePhoenixRi"})
    @DisplayName("Test fetchPublicProfile GET endpoint by validating username, it should return 200 ")
    void testFetchPublicProfile_whenValidUsernameProvided_shouldReturn200(String username) {
        // Arrange
        var publicUserDto = new PublicUserResponseDto(UUID.randomUUID(), username, "kaylin", "", "");
        when(userService.fetchPublicProfile(username)).thenReturn(publicUserDto);

        // Act
        MvcTestResult result = mockMvcTester.get().uri(baseURI + "/" + username)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("code").isEqualTo(SuccessfulCode.USER_FOUND.toString());
                    assertThat(json).extractingPath("status").isEqualTo(HttpStatus.OK.value());
                    assertThat(json).extractingPath("data").convertTo(PublicUserResponseDto.class).isEqualTo(publicUserDto);
                });
        verify(userService).fetchPublicProfile(username);

    }


    @ParameterizedTest
    @CsvSource({"Us",
            "Alpha`Fox",
            "GamerTag{99",
            "+_",
            "+pp",
            "|sssakk",
            "PhoeniPhoenixRisePhoenixRise"})
    @DisplayName("Test fetchPublicProfile GET endpoint by sending invalid username, it should return 400 ")
    void testFetchPublicProfile_whenInvalidUsernameProvided_shouldReturn400(String username) {
        // Arrange
        var publicUserDto = new PublicUserResponseDto(UUID.randomUUID(), username, "kaylin", "", "");
        when(userService.fetchPublicProfile(username)).thenReturn(publicUserDto);

        // Act
        MvcTestResult result = mockMvcTester.get().uri(baseURI + "/" + username)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("code").isEqualTo(ErrorCode.VALIDATION_ERROR.toString());
                    assertThat(json).extractingPath("status").isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(json).extractingPath("detail").isNotEmpty();
                    assertThat(json).extractingPath("error").isNotEmpty();
                });
        verify(userService, never()).fetchPublicProfile(any());

    }


    @Test
    @DisplayName("Test fetchMe GET endpoint by assuring it returns USER_PROFILE_COMPLETE")
    void testFetchMe_whenAlreadyOnBoardedUserProvided_shouldReturnUSER_PROFILE_COMPLETE() {
        // Arrange
        var userDto = UserDto.builder()
                .providerId(UUID.randomUUID().toString())
                .username("kaylin")
                .displayName("Kaylin Fitz")
                .email("kaylin@gmail.com")
                .mobileNumber("666444333")
                .profileBio("nothing")
                .profileImageUrl("")
                .build();
        when(userService.getAuthenticatedDto(any(Jwt.class))).thenReturn(Optional.of(userDto));

        // Act
        MvcTestResult result = mockMvcTester.get().uri(baseURI + "/me")
                .accept(MediaType.APPLICATION_JSON)
                .with(jwt())
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("code").isEqualTo(SuccessfulCode.USER_PROFILE_COMPLETE.toString());
                    assertThat(json).extractingPath("status").isEqualTo(HttpStatus.OK.value());
                    assertThat(json).extractingPath("detail").isNotEmpty();
                    assertThat(json).extractingPath("data").convertTo(UserDto.class).isEqualTo(userDto);
                });
        verify(userService).getAuthenticatedDto(any(Jwt.class));
    }


    @Test
    @DisplayName("Test fetchMe GET endpoint by assuring it returns ONBOARDING_REQUIRED")
    void testFetchMe_whenNonOnBoardedUserProvided_shouldReturnONBOARDING_REQUIRED() {
        // Arrange
        when(userService.getAuthenticatedDto(any(Jwt.class))).thenReturn(Optional.empty());

        // Act
        MvcTestResult result = mockMvcTester.get().uri(baseURI + "/me")
                .accept(MediaType.APPLICATION_JSON)
                .with(jwt())
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("code").isEqualTo(SuccessfulCode.ONBOARDING_REQUIRED.toString());
                    assertThat(json).extractingPath("status").isEqualTo(HttpStatus.OK.value());
                    assertThat(json).extractingPath("detail").isNotEmpty();
                    assertThat(json).extractingPath("data").isNull();
                });
        verify(userService).getAuthenticatedDto(any(Jwt.class));
    }


    @Test
    @DisplayName("Test fetchMe GET endpoint by not sending authentication, should return 401")
    void testFetchMe_whenNoAuthenticationProvided_shouldReturn401() {
        // Arrange

        // Act
        MvcTestResult result = mockMvcTester.get().uri(baseURI + "/me")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.UNAUTHORIZED);
        verify(userService, never()).getAuthenticatedDto(any());
    }


    @Test
    @DisplayName("Test updateUserDetails PATCH endpoint by not sending authentication, should return 401")
    void testUpdateUserDetails_whenNoAuthenticationProvided_shouldReturn401() {
        // Arrange
        var updateProfileDto = new UpdateUserProfileRequestDto("Laura", "888777666", "nothing", "");

        // Act

        MvcTestResult result = mockMvcTester.patch().uri(baseURI + "/me")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProfileDto))
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.UNAUTHORIZED);
        verify(userService, never()).updateUserDetails(any(), any(Jwt.class));
    }


    @Test
    @DisplayName("Test updateUserDetails PATCH endpoint by sending correct update details, should return USER_UPDATED")
    void testUpdateUserDetails_whenCorrectDetailsProvided_shouldReturnUSER_UPDATED() {
        // Arrange
        var updateProfileDto = new UpdateUserProfileRequestDto("kaylin1", "888777666", null, null);
        var userDto = UserDto.builder()
                .providerId(UUID.randomUUID().toString())
                .username("kaylin1")
                .displayName("Kaylin Fitz")
                .email("kaylin@gmail.com")
                .mobileNumber("888777666")
                .profileBio("nothing")
                .profileImageUrl("")
                .build();
        when(userService.updateUserDetails(eq(updateProfileDto), any(Jwt.class))).thenReturn(userDto);

        // Act

        MvcTestResult result = mockMvcTester.patch().uri(baseURI + "/me")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProfileDto))
                .with(jwt())
                .exchange();

        // Assert
        assertThat(result)
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .satisfies(json -> {
                    assertThat(json).extractingPath("code").isEqualTo(SuccessfulCode.USER_UPDATED.toString());
                    assertThat(json).extractingPath("status").isEqualTo(HttpStatus.OK.value());
                    assertThat(json).extractingPath("detail").isNotEmpty();
                    assertThat(json).extractingPath("data").convertTo(UserDto.class).isEqualTo(userDto);
                });
        verify(userService).updateUserDetails(eq(updateProfileDto), any(Jwt.class));
    }
}
