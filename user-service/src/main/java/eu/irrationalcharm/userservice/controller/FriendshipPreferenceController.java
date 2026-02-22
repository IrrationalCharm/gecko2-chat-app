package eu.irrationalcharm.userservice.controller;

import eu.irrationalcharm.dto.response.SuccessResponseDto;
import eu.irrationalcharm.enums.SuccessfulCode;
import eu.irrationalcharm.validation.UsernameValid;
import eu.irrationalcharm.userservice.dto.response.FriendPreferenceDto;
import eu.irrationalcharm.userservice.dto.response.PatchFriendPreferenceDto;
import eu.irrationalcharm.userservice.dto.response.base.ApiResponse;
import eu.irrationalcharm.userservice.service.orchestrator.UpdateFriendPreferenceOrchestrator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/friend-preference")
public class FriendshipPreferenceController {

    private final UpdateFriendPreferenceOrchestrator ufpOrchestrator;

    @GetMapping("/{username}")
    public ResponseEntity<SuccessResponseDto<FriendPreferenceDto>> getFriendPreference(@PathVariable @UsernameValid String username,
                                                                                       @AuthenticationPrincipal Jwt jwt,
                                                                                       HttpServletRequest request) {
        var friendPreferenceDto = ufpOrchestrator.getFriendPreferenceOrThrow(jwt, username);


        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.FRIEND_PREFERENCE_FOUND,
                "Found Users Friend Preference",
                friendPreferenceDto,
                request);
    }


    //TODO: Replace username to user internalId
    @PatchMapping("/{username}")
    public ResponseEntity<SuccessResponseDto<PatchFriendPreferenceDto>> patchFriendPreference(@PathVariable @UsernameValid String username,
                                                                                              @RequestBody PatchFriendPreferenceDto friendPreference,
                                                                                              @AuthenticationPrincipal Jwt jwt,
                                                                                              HttpServletRequest request) {
        log.info("Request received to patch friend preferences to target Username: {}", username);
        var patchedFriendPreferenceDto = ufpOrchestrator.updateFriendPreference(jwt, username, friendPreference);

        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.FRIEND_PREFERENCE_UPDATED,
                "Users friend preference updated successfully",
                patchedFriendPreferenceDto,
                request
        );
    }
}
