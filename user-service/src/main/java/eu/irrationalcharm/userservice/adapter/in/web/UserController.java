package eu.irrationalcharm.userservice.adapter.in.web;

import eu.irrationalcharm.dto.response.SuccessResponseDto;
import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.dto.user_service.UserDto;
import eu.irrationalcharm.enums.ErrorCode;
import eu.irrationalcharm.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.application.port.in.GetUserUseCase;
import eu.irrationalcharm.userservice.application.port.in.UpdateUserUseCase;
import eu.irrationalcharm.userservice.constants.JwtClaims;
import eu.irrationalcharm.userservice.dto.request.UpdateUserProfileRequestDto;
import eu.irrationalcharm.userservice.dto.response.base.ApiResponse;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.validation.UsernameValid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;

    @GetMapping("/{username}")
    public ResponseEntity<SuccessResponseDto<PublicUserResponseDto>> fetchPublicProfile(
            @PathVariable @UsernameValid String username,
            HttpServletRequest request) {

        PublicUserResponseDto publicUser = getUserUseCase.getPublicProfile(username);

        return ApiResponse.success(HttpStatus.OK, SuccessfulCode.USER_FOUND, "User has been found", publicUser, request);
    }

    @GetMapping("/me")
    public ResponseEntity<SuccessResponseDto<UserDto>> fetchMe(
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest request) {

        String userIdStr = jwt.getClaimAsString(JwtClaims.INTERNAL_ID);
        UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;

        Optional<UserDto> optionalUserDto = getUserUseCase.getAuthenticatedUserDto(userId);

        SuccessfulCode successfulCode;
        UserDto userDto = null;

        if (optionalUserDto.isPresent()) {
            successfulCode = SuccessfulCode.USER_PROFILE_COMPLETE;
            userDto = optionalUserDto.get();
        } else {
            successfulCode = SuccessfulCode.ONBOARDING_REQUIRED;
        }

        return ApiResponse.success(HttpStatus.OK, successfulCode, "Successfully retrieved user status", userDto, request);
    }

    @PatchMapping("/me")
    public ResponseEntity<SuccessResponseDto<UserDto>> updateUserDetails(
            @RequestBody UpdateUserProfileRequestDto userProfileRequestDto,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest request) {

        UUID userId = extractUserIdOrThrow(jwt);
        log.info("Request received to update user details for user internalId: {}", userId);

        UserDto userDto = updateUserUseCase.updateUserDetails(userId, userProfileRequestDto);

        return ApiResponse.success(HttpStatus.OK, SuccessfulCode.USER_UPDATED, "Updated Successfully", userDto, request);
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
