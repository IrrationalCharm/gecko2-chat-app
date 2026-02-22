package eu.irrationalcharm.userservice.controller;


import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.dto.user_service.UserDto;
import eu.irrationalcharm.dto.response.SuccessResponseDto;
import eu.irrationalcharm.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.constants.JwtClaims;
import eu.irrationalcharm.validation.UsernameValid;
import eu.irrationalcharm.userservice.dto.request.UpdateUserProfileRequestDto;
import eu.irrationalcharm.userservice.dto.response.base.ApiResponse;
import eu.irrationalcharm.userservice.service.UserService;
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


@Slf4j
@RestController
@Validated
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/{username}")
    public ResponseEntity<SuccessResponseDto<PublicUserResponseDto>> fetchPublicProfile(@PathVariable @UsernameValid String username,
                                                                                        HttpServletRequest request) {

        PublicUserResponseDto publicUser = userService.fetchPublicProfile(username);

        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.USER_FOUND,
                "User has been found",
                publicUser,
                request
        );
    }


    @GetMapping("/me")
    public ResponseEntity<SuccessResponseDto<UserDto>> fetchMe(@AuthenticationPrincipal Jwt authJwt,
                                                               HttpServletRequest request) {

        Optional<UserDto> optionalUserDto = userService.getAuthenticatedDto(authJwt);
        SuccessfulCode successfulCode;
        UserDto userDto = null;

        if ( optionalUserDto.isPresent() ) {
            successfulCode = SuccessfulCode.USER_PROFILE_COMPLETE;
            userDto = optionalUserDto.get();
        } else
            successfulCode = SuccessfulCode.ONBOARDING_REQUIRED;

        return ApiResponse.success(HttpStatus.OK,
                successfulCode,
                "Successfully retrieved user status",
                userDto,
                request);
    }


    @PatchMapping("/me")
    public ResponseEntity<SuccessResponseDto<UserDto>> updateUserDetails(@RequestBody UpdateUserProfileRequestDto userProfileRequestDto,
                                                                         @AuthenticationPrincipal Jwt jwt,
                                                                         HttpServletRequest request) {
        log.info("Request received to update user details for user internalId: {}", jwt.getClaim(JwtClaims.INTERNAL_ID).toString());

        UserDto userDto = userService.updateUserDetails(userProfileRequestDto, jwt);
        return ApiResponse.success(HttpStatus.OK,
                SuccessfulCode.USER_UPDATED,
                "Updated Successfully",
                userDto,
                request);
    }
}
