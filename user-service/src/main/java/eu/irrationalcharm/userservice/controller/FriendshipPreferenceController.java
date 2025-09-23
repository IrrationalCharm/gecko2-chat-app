package eu.irrationalcharm.userservice.controller;

import eu.irrationalcharm.userservice.dto.response.FriendPreferenceDto;
import eu.irrationalcharm.userservice.dto.response.PatchFriendPreferenceDto;
import eu.irrationalcharm.userservice.dto.response.base.ApiResponse;
import eu.irrationalcharm.userservice.dto.response.base.SuccessResponseDto;
import eu.irrationalcharm.userservice.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.service.UserFriendshipPreferenceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    private final UserFriendshipPreferenceService ufpService;

    @GetMapping("/{username}")
    public ResponseEntity<SuccessResponseDto<FriendPreferenceDto>> getFriendPreference(@PathVariable @NotNull @Size(min = 3, max = 20, message = "Username must be between 3 - 20") String username,
                                                                                       @AuthenticationPrincipal Jwt jwt,
                                                                                       HttpServletRequest request) {
        var friendPreferenceDto = ufpService.getFriendPreferenceOrThrow(jwt, username);

        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.FRIEND_PREFERENCE_FOUND,
                "Found Users Friend Preference",
                friendPreferenceDto,
                request);
    }


    @PatchMapping("/{username}")
    public ResponseEntity<SuccessResponseDto<PatchFriendPreferenceDto>> patchFriendPreference(@PathVariable @NotNull @Size(min = 3, max = 20, message = "Username must be between 3 - 20") String username,
                                                                                              @RequestBody PatchFriendPreferenceDto friendPreference,
                                                                                              @AuthenticationPrincipal Jwt jwt,
                                                                                              HttpServletRequest request) {
        var patchFriendPreferenceDto = ufpService.updateFriendPreference(jwt, username, friendPreference);

        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.FRIEND_PREFERENCE_UPDATED,
                "Users friend preference updated successfully",
                patchFriendPreferenceDto,
                request
        );
    }
}
