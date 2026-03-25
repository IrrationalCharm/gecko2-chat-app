package eu.irrationalcharm.userservice.adapter.in.web;

import eu.irrationalcharm.dto.response.SuccessResponseDto;
import eu.irrationalcharm.enums.ErrorCode;
import eu.irrationalcharm.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.application.port.in.ManageFriendPreferenceUseCase;
import eu.irrationalcharm.userservice.constants.JwtClaims;
import eu.irrationalcharm.userservice.dto.response.FriendPreferenceDto;
import eu.irrationalcharm.userservice.dto.response.PatchFriendPreferenceDto;
import eu.irrationalcharm.userservice.dto.response.base.ApiResponse;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.validation.UsernameValid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/friend-preference")
public class FriendshipPreferenceController {

    private final ManageFriendPreferenceUseCase manageFriendPreferenceUseCase;

    @GetMapping("/{username}")
    public ResponseEntity<SuccessResponseDto<FriendPreferenceDto>> getFriendPreference(
            @PathVariable @UsernameValid String username,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest request) {

        UUID userId = extractUserIdOrThrow(jwt);
        FriendPreferenceDto friendPreferenceDto = manageFriendPreferenceUseCase.getFriendPreference(userId, username);

        return ApiResponse.success(HttpStatus.OK, SuccessfulCode.FRIEND_PREFERENCE_FOUND,
                "Found Users Friend Preference", friendPreferenceDto, request);
    }

    @PatchMapping("/{username}")
    public ResponseEntity<SuccessResponseDto<PatchFriendPreferenceDto>> patchFriendPreference(
            @PathVariable @UsernameValid String username,
            @RequestBody PatchFriendPreferenceDto friendPreference,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest request) {

        UUID userId = extractUserIdOrThrow(jwt);
        log.info("Request received to patch friend preferences to target Username: {}", username);

        PatchFriendPreferenceDto result = manageFriendPreferenceUseCase.updateFriendPreference(userId, username, friendPreference);

        return ApiResponse.success(HttpStatus.OK, SuccessfulCode.FRIEND_PREFERENCE_UPDATED,
                "Users friend preference updated successfully", result, request);
    }

    private UUID extractUserIdOrThrow(Jwt jwt) {
        String userIdStr = jwt.getClaimAsString(JwtClaims.INTERNAL_ID);
        if (userIdStr == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.ON_BOARDING_REQUIRED,
                    "Empty internal_id in claim");
        }
        return UUID.fromString(userIdStr);
    }
}
