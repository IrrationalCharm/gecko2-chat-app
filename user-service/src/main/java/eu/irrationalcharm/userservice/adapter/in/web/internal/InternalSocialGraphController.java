package eu.irrationalcharm.userservice.adapter.in.web.internal;

import eu.irrationalcharm.userservice.application.port.in.GetSocialGraphUseCase;
import eu.irrationalcharm.userservice.constants.JwtClaims;
import eu.irrationalcharm.userservice.dto.internal.response.UserSocialGraphDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/api/v1/social-graph")
public class InternalSocialGraphController {

    private final GetSocialGraphUseCase getSocialGraphUseCase;

    @GetMapping
    public ResponseEntity<UserSocialGraphDto> getAuthenticatedSocialGraph(@AuthenticationPrincipal Jwt jwt) {
        String userIdStr = jwt.getClaimAsString(JwtClaims.INTERNAL_ID);
        UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;

        UserSocialGraphDto userSocialGraphDto = getSocialGraphUseCase.getSocialGraph(userId);
        return ResponseEntity.ok(userSocialGraphDto);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<UserSocialGraphDto> getSocialGraphByUsername(@PathVariable String username) {
        UserSocialGraphDto userSocialGraphDto = getSocialGraphUseCase.getSocialGraphByUsername(username);
        return ResponseEntity.ok(userSocialGraphDto);
    }
}
