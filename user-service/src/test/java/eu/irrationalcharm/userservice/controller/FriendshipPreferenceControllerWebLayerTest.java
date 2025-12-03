package eu.irrationalcharm.userservice.controller;

import eu.irrationalcharm.userservice.dto.response.FriendPreferenceDto;
import eu.irrationalcharm.userservice.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.service.orchestrator.UpdateFriendPreferenceOrchestrator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {FriendshipPreferenceController.class})
@ActiveProfiles("test")
class FriendshipPreferenceControllerWebLayerTest {

    @MockitoBean
    private UpdateFriendPreferenceOrchestrator ufpOrchestrator;

    @Autowired
    private MockMvc mockMvc;

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

}
