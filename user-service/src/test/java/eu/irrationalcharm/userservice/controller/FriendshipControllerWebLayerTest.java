package eu.irrationalcharm.userservice.controller;

import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.enums.ErrorCode;
import eu.irrationalcharm.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.config.security.SecurityConfig;
import eu.irrationalcharm.dto.user_service.FriendRequestDto;
import eu.irrationalcharm.userservice.dto.request.UpdateFriendRequestDto;
import eu.irrationalcharm.userservice.enums.FriendRequestAction;
import eu.irrationalcharm.userservice.service.FriendRequestService;
import eu.irrationalcharm.userservice.service.orchestrator.FriendshipOrchestrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import tools.jackson.databind.json.JsonMapper;


import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(value = FriendshipController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class FriendshipControllerWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private FriendRequestService friendRequestService;

    @MockitoBean
    private FriendshipOrchestrator friendshipOrchestrator;

    //prevents spring from fetching the issuer-uri from keycloak
    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    private static final String baseFriendsUrl = "/api/v1/friends";


    @BeforeEach
    void setup() {

    }


    @Test
    @DisplayName("Test getFriends returns Http response 200 with correct data")
    void testGetFriends_whenFriendsListRequested_shouldReturn200WithUsersFriends() throws Exception {
        // Arrange
        var friend = PublicUserResponseDto.builder()
                .internalId(UUID.randomUUID())
                .username("dominik.fitz")
                .displayName("Dominik Fitz")
                .profileBio("The best")
                .profileImageUrl("https://image.com")
                .build();

        var friend2 = PublicUserResponseDto.builder()
                .internalId(UUID.randomUUID())
                .username("kaylinita")
                .displayName("Kaylin Fitz")
                .profileBio("The second best")
                .profileImageUrl("https://image2.com")
                .build();

        when(friendshipOrchestrator.getFriends(any())).thenReturn(Set.of(friend, friend2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/friends")
                    .with(jwt())
                    .contentType(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.code").value(SuccessfulCode.FRIENDS_LIST.toString()))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[*].username", containsInAnyOrder(friend.username(), friend2.username())))
                .andExpect(jsonPath("$.data[*].displayName", containsInAnyOrder(friend.displayName(), friend2.displayName())));

        verify(friendshipOrchestrator).getFriends(any(Jwt.class));
    }


    @ParameterizedTest
    @MethodSource("publicEndpointsProvider")
    @DisplayName("Test all endpoints returns 401 when no Authorization header provided")
    void testGetFriends_whenNoAuthenticationProvided_shouldReturn401UnauthorizedError(HttpMethod method, String endpoint) throws Exception {
        // Arrange
        RequestBuilder request = request(method, endpoint)
                .contentType(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(friendRequestService, friendshipOrchestrator);

    }

    private static Stream<Arguments> publicEndpointsProvider() {
        return Stream.of(
                Arguments.of(HttpMethod.GET, baseFriendsUrl),
                Arguments.of(HttpMethod.GET, baseFriendsUrl + "/requests"),
                Arguments.of(HttpMethod.POST, baseFriendsUrl + "/requests/kaylin"),
                Arguments.of(HttpMethod.DELETE, baseFriendsUrl + "/kaylinita123"),
                Arguments.of(HttpMethod.PATCH, baseFriendsUrl + "/requests")
        );
    }


    @Test
    @DisplayName("Test pendingFriendRequests returns Http response 200 with correct data")
    void testPendingFriendRequests_whenRequestedPendingFriendRequest_shouldReturn200WithPendingFriendRequests() throws Exception {
        // Arrange
        var friendRequest = FriendRequestDto.builder()
                .id(1)
                .initiatorId(UUID.randomUUID())
                .receiverId(UUID.randomUUID())
                .initiatorUsername("dominik.fitz")
                .initiatorDisplayName("Dominik Fitz")
                .initiatorUrlProfileImage("https://image.com")
                .createdAt(Instant.now())
                .build();

        var friendRequest2 = FriendRequestDto.builder()
                .id(2)
                .initiatorId(UUID.randomUUID())
                .receiverId(UUID.randomUUID())
                .initiatorUsername("kaylinita")
                .initiatorDisplayName("Kaylin Fitz")
                .initiatorUrlProfileImage("https://image2.com")
                .createdAt(Instant.now())
                .build();

        when(friendRequestService.getPendingFriendRequests(any(Jwt.class))).thenReturn(List.of(friendRequest, friendRequest2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/friends/requests")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.code").value(SuccessfulCode.FRIEND_REQUEST_PENDING.toString()))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[*].initiatorUsername", containsInAnyOrder(friendRequest.initiatorUsername(), friendRequest2.initiatorUsername())))
                .andExpect(jsonPath("$.data[*].initiatorDisplayName", containsInAnyOrder(friendRequest.initiatorDisplayName(), friendRequest2.initiatorDisplayName())));

        verify(friendRequestService).getPendingFriendRequests(any(Jwt.class));
    }


    @ParameterizedTest
    @ValueSource(strings = {"User_123", "AlphaFox", "GamerTag-99", "L33t_Hax0r", "orl", "dataM1ner", "PhoenixRisePhoenixRi"})
    @DisplayName("Test sendFriendRequest validates username and returns Http code 201")
    void testSendFriendRequest_whenFriendRequestIsSent_shouldCorrectlyValidateUsernameAndReturn201(String username) throws Exception {
        // Arrange
        doNothing().when(friendshipOrchestrator).sendFriendRequest(any(), eq(username));

        // Act & Assert
        mockMvc.perform(post("/api/v1/friends/requests/" + username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(jwt()))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.code").value(SuccessfulCode.FRIEND_REQUEST_SENT.toString()))
                .andExpect(jsonPath("$.data", nullValue()));

        verify(friendshipOrchestrator, times(1)).sendFriendRequest(any(), eq(username));
    }


    @ParameterizedTest
    @ValueSource(strings = {"Us", "Alpha`Fox", "GamerTag{99", "   ", "+pp", "|sssakk", "PhoenixRisePhoenixRise"})
    @DisplayName("Test post sendFriendRequest endpoint by sending invalid usernames")
    void testSendFriendRequest_whenInvalidUsernameSent_shouldReturnBadRequest400(String username) throws Exception {
        // Arrange
        doNothing().when(friendshipOrchestrator).sendFriendRequest(any(Jwt.class), eq(username));

        // Act & Assert
        mockMvc.perform(post("/api/v1/friends/requests/" + username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(jwt()))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.toString()))
                .andExpect(jsonPath("$.error", notNullValue()));

        verify(friendshipOrchestrator, never()).sendFriendRequest(any(Jwt.class), eq(username));
    }


    @ParameterizedTest
    @ValueSource(strings = {"User_123", "AlphaFox", "GamerTag-99", "L33t_Hax0r", "orl", "dataM1ner", "PhoenixRisePhoenixRi"})
    @DisplayName("Test removeFriend validates username and returns Http code 200")
    void testRemoveFriend_whenRemoveRequestSent_shouldReturnHttpCode200(String username) throws Exception {
        // Arrange
        doNothing().when(friendshipOrchestrator).removeFriend(any(Jwt.class), eq(username));

        // Act & Assert
        mockMvc.perform(delete("/api/v1/friends/" + username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(jwt()))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.code").value(SuccessfulCode.FRIEND_REMOVED.toString()))
                .andExpect(jsonPath("$.data", nullValue()));

        verify(friendshipOrchestrator).removeFriend(any(Jwt.class), eq(username));
    }


    @ParameterizedTest
    @ValueSource(strings = {"Us", "Alpha`Fox", "GamerTag{99", "   ", "+pp", "|sssakk", "PhoenixRisePhoenixRise", "lazurus 22"})
    @DisplayName("Test removeFriend /requests/username endpoint by sending invalid usernames")
    void testRemoveFriend_whenInvalidUsernameSent_shouldReturnBadRequest400(String username) throws Exception {
        // Arrange
        doNothing().when(friendshipOrchestrator).removeFriend(any(Jwt.class), eq(username));

        // Act & Assert
        mockMvc.perform(delete("/api/v1/friends/" + username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(jwt()))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.code").value(ErrorCode.VALIDATION_ERROR.toString()))
                .andExpect(jsonPath("$.error", notNullValue()));

        verify(friendshipOrchestrator, never()).removeFriend(any(Jwt.class), eq(username));
    }



    @Test
    @DisplayName("Test updateFriendRequest endpoint and validating request and username")
    void testUpdateFriendRequest_whenUpdateFriendRequestSent_shouldReturnOk200() throws Exception {
        // Arrange
        var friendRequestDto = new UpdateFriendRequestDto(FriendRequestAction.ACCEPT_REQUEST);
        long requestId = 1L;
        when(friendshipOrchestrator.updateFriendRequest(any(Jwt.class), eq(requestId), eq(friendRequestDto))).thenReturn(SuccessfulCode.FRIEND_REQUEST_ACCEPTED);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/friends/requests/{requestId}", requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(friendRequestDto))
                        .with(jwt()))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.code").value(SuccessfulCode.FRIEND_REQUEST_ACCEPTED.toString()))
                .andExpect(jsonPath("$.data", nullValue()));

        verify(friendshipOrchestrator).updateFriendRequest(any(Jwt.class), eq(requestId), eq(friendRequestDto));
    }
}
