package eu.irrationalcharm.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.irrationalcharm.userservice.config.security.SecurityConfig;
import eu.irrationalcharm.userservice.dto.response.FriendPreferenceDto;
import eu.irrationalcharm.userservice.dto.response.PatchFriendPreferenceDto;
import eu.irrationalcharm.userservice.enums.ErrorCode;
import eu.irrationalcharm.userservice.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.service.orchestrator.UpdateFriendPreferenceOrchestrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {FriendshipPreferenceController.class})
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class FriendshipPreferenceControllerWebLayerTest {

    @MockitoBean
    private UpdateFriendPreferenceOrchestrator ufpOrchestrator;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    @ParameterizedTest
    @ValueSource(strings = {"User_123", "AlphaFox", "GamerTag-99", "L33t_Hax0r", "orl", "dataM1ner", "PhoenixRisePhoenixRi"})
    @DisplayName("Test getFriendPreference GET endpoint by validating username and it should return 200")
    void testGetFriendPreference_whenGetFriendPreferenceRequestSent_shouldValidateUsernameAndReturn200(String username) throws Exception {
        // Arrange
        var friendPreferenceDto = new FriendPreferenceDto(
                username,
                false,
                false,
                true);
        when(ufpOrchestrator.getFriendPreferenceOrThrow(any(Jwt.class), eq(username))).thenReturn(friendPreferenceDto);

        // Act & Assert
        mockMvc.perform(get("/api/v1/friend-preference/" + username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(jwt()))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessfulCode.FRIEND_PREFERENCE_FOUND.toString()))
                .andExpect(jsonPath("$.data.friendUsername", is(username)))
                .andExpect(jsonPath("$.data.isBlocked", is(friendPreferenceDto.isBlocked())))
                .andExpect(jsonPath("$.data.isMuted", is(friendPreferenceDto.isMuted())))
                .andExpect(jsonPath("$.data.isPinned", is(friendPreferenceDto.isPinned())));

        verify(ufpOrchestrator).getFriendPreferenceOrThrow(any(Jwt.class), eq(username));
    }


    @Test
    @DisplayName("Test getFriendPreference returns 401 when no Authorization header provided")
    void testGetFriendPreference_whenNoAuthenticationProvided_shouldReturn401UnauthorizedError() throws Exception {
        // Arrange
        String username = "kaylinita1";
        var friendPreferenceDto = new FriendPreferenceDto(
                username,
                false,
                false,
                true);
        when(ufpOrchestrator.getFriendPreferenceOrThrow(any(Jwt.class), eq(username))).thenReturn(friendPreferenceDto);

        // Act & Assert
        mockMvc.perform(get("/api/v1/friend-preference/" + username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isUnauthorized());

        verify(ufpOrchestrator, never()).getFriendPreferenceOrThrow(any(Jwt.class), eq(username));
    }



    @ParameterizedTest
    @ValueSource(strings = {"Us", "Alpha`Fox", "GamerTag{99", "   ", "+pp", "|sssakk", "PhoenixRisePhoenixRise", "dominik fitz"})
    @DisplayName("Test post sendFriendRequest endpoint by sending invalid usernames")
    void testGetFriendPreference_whenInvalidUsernameSent_shouldReturnBadRequest400(String username) throws Exception {
        // Arrange
        var friendPreferenceDto = new FriendPreferenceDto(
                username,
                false,
                false,
                true);
        when(ufpOrchestrator.getFriendPreferenceOrThrow(any(Jwt.class), eq(username))).thenReturn(friendPreferenceDto);

        // Act & Assert
        mockMvc.perform(get("/api/v1/friend-preference/" + username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(jwt()))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.toString()))
                .andExpect(jsonPath("$.error", notNullValue()));

        verify(ufpOrchestrator, never()).getFriendPreferenceOrThrow(any(Jwt.class), eq(username));
    }


    @ParameterizedTest
    @ValueSource(strings = {"User_123", "AlphaFox", "GamerTag-99", "L33t_Hax0r", "orl", "dataM1ner", "PhoenixRisePhoenixRi"})
    @DisplayName("Test patchFriendPreference PATCH endpoint by validating username and it should return 200")
    void testPatchFriendPreference_whenPatchFriendPreferenceRequestSent_shouldValidateUsernameAndReturn200(String username) throws Exception {
        // Arrange
        var patchPreferenceDto = new PatchFriendPreferenceDto(
                false,
                false,
                true);
        when(ufpOrchestrator.updateFriendPreference(any(Jwt.class), eq(username), eq(patchPreferenceDto))).thenReturn(patchPreferenceDto);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/friend-preference/" + username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchPreferenceDto))
                        .with(jwt()))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(SuccessfulCode.FRIEND_PREFERENCE_UPDATED.toString()))
                .andExpect(jsonPath("$.data.isBlocked", is(patchPreferenceDto.isBlocked())))
                .andExpect(jsonPath("$.data.isMuted", is(patchPreferenceDto.isMuted())))
                .andExpect(jsonPath("$.data.isPinned", is(patchPreferenceDto.isPinned())));

        verify(ufpOrchestrator).updateFriendPreference(any(Jwt.class), eq(username), eq(patchPreferenceDto));
    }


    @ParameterizedTest
    @ValueSource(strings = {"Us", "Alpha`Fox", "GamerTag{99", "   ", "+pp", "|sssakk", "PhoenixRisePhoenixRise", "dominik fitz"})
    @DisplayName("Test post patchFriendPreference PATCH endpoint by sending invalid usernames")
    void testPatchFriendPreference_whenInvalidUsernameSent_shouldReturnBadRequest400(String username) throws Exception {
        // Arrange
        var patchPreferenceDto = new PatchFriendPreferenceDto(
                false,
                false,
                true);
        when(ufpOrchestrator.updateFriendPreference(any(Jwt.class), eq(username), eq(patchPreferenceDto))).thenReturn(patchPreferenceDto);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/friend-preference/" + username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchPreferenceDto))
                        .with(jwt()))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.toString()))
                .andExpect(jsonPath("$.error", notNullValue()));

        verify(ufpOrchestrator, never()).updateFriendPreference(any(Jwt.class), eq(username), eq(patchPreferenceDto));
    }


    @Test
    @DisplayName("Test patchFriendPreference returns 401 when no Authorization header provided")
    void testPatchFriendPreference_whenNoAuthenticationProvided_shouldReturn401UnauthorizedError() throws Exception {
        // Arrange
        String username = "matiusYoky";
        var friendPreferenceDto = new FriendPreferenceDto(
                username,
                false,
                false,
                true);
        when(ufpOrchestrator.getFriendPreferenceOrThrow(any(Jwt.class), eq(username))).thenReturn(friendPreferenceDto);

        // Act & Assert
        mockMvc.perform(get("/api/v1/friend-preference/" + username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isUnauthorized());

        verify(ufpOrchestrator, never()).getFriendPreferenceOrThrow(any(Jwt.class), eq(username));
    }

}
