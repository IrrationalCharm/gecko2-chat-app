package eu.irrationalcharm.userservice.controller;

import eu.irrationalcharm.userservice.annotation.UsernameValid;
import eu.irrationalcharm.userservice.dto.response.FriendPreferenceDto;
import eu.irrationalcharm.userservice.dto.response.PatchFriendPreferenceDto;
import eu.irrationalcharm.userservice.dto.response.base.ApiResponse;
import eu.irrationalcharm.userservice.dto.response.base.SuccessResponseDto;
import eu.irrationalcharm.userservice.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.service.orchestrator.UpdateFriendPreferenceOrchestrator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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


    @PatchMapping("/{username}")
    public ResponseEntity<SuccessResponseDto<PatchFriendPreferenceDto>> patchFriendPreference(@PathVariable @UsernameValid String username,
                                                                                              @RequestBody PatchFriendPreferenceDto friendPreference,
                                                                                              @AuthenticationPrincipal Jwt jwt,
                                                                                              HttpServletRequest request) {
        var patchFriendPreferenceDto = ufpOrchestrator.updateFriendPreference(jwt, username, friendPreference);

        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.FRIEND_PREFERENCE_UPDATED,
                "Users friend preference updated successfully",
                patchFriendPreferenceDto,
                request
        );
    }
}
