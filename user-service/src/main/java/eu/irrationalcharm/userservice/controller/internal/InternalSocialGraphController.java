package eu.irrationalcharm.userservice.controller.internal;


import eu.irrationalcharm.userservice.dto.internal.response.UserSocialGraphDto;
import eu.irrationalcharm.userservice.service.orchestrator.InternalSocialGraphOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/api/v1/social-graph")
public class InternalSocialGraphController {

    private final InternalSocialGraphOrchestrator socialGraphOrchestrator;

    @GetMapping
    public ResponseEntity<UserSocialGraphDto> getSocialGraph(@AuthenticationPrincipal Jwt jwt){
        UserSocialGraphDto userSocialGraphDto = socialGraphOrchestrator.getSocialGraph(jwt);

        return ResponseEntity.ok(userSocialGraphDto);
    }

}
