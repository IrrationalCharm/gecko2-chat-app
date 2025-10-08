package eu.irrationalcharm.userservice.client;

import eu.irrationalcharm.userservice.constants.JwtClaims;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.userservice.enums.ErrorCode;
import eu.irrationalcharm.userservice.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import static eu.irrationalcharm.userservice.constants.JwtClaims.*;
import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakAdminClient {

    private final RestClient restClient;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.baseUri}")
    private String baseUri;


    /**
     * Sets the internal_id and username attributes for a user in Keycloak.
     * Done after onBoarding thus including the internalId and username in the JWT.
     * @param keycloakUserId Keycloak sub
     * @param userEntity onboarding user
     */
    public void addUserAttributes(String keycloakUserId, UserEntity userEntity) {

        var attributes = Map.of(
                INTERNAL_ID, List.of(userEntity.getId()),
                USERNAME_APP, List.of(userEntity.getUsername())
        );

        var body = Map.of(
                EMAIL, userEntity.getEmail(), //Needed for keycloak
                ATTRIBUTES, attributes
        );

        try {
            restClient.put()
                    .uri(String.format("%s/admin/realms/%s/users/%s", baseUri, realm, keycloakUserId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .attributes(clientRegistrationId("keycloak-admin")) //tells the interceptor that requests the access token to get the one specified in the properties file keycloak-admin
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Failed to update Keycloak user attributes for user {}: {}", keycloakUserId, e.getMessage());
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.KEYCLOAK_API_ERROR, "An internal error occurred while finalizing user registration.");
        }

    }
}
