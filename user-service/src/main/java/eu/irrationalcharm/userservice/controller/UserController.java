package eu.irrationalcharm.userservice.controller;


import eu.irrationalcharm.userservice.dto.request.UpdateUserProfileRequestDto;
import eu.irrationalcharm.userservice.dto.response.PublicUserResponseDto;
import eu.irrationalcharm.userservice.dto.UserDto;
import eu.irrationalcharm.userservice.dto.response.base.ApiResponse;
import eu.irrationalcharm.userservice.dto.response.base.SuccessResponseDto;
import eu.irrationalcharm.userservice.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/{username}")
    public ResponseEntity<SuccessResponseDto<PublicUserResponseDto>> fetchPublicProfile(@PathVariable String username, HttpServletRequest request) {

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
    public ResponseEntity<SuccessResponseDto<UserDto>> fetchMe(@AuthenticationPrincipal Jwt authJwt, HttpServletRequest request) {

        Optional<UserDto> optionalUserDto = userService.fetchMe(authJwt);
        SuccessfulCode successfulCode;
        UserDto userDto = null;

        if ( optionalUserDto.isPresent() ) {
            successfulCode = SuccessfulCode.USER_PROFILE_COMPLETE;
            userDto = optionalUserDto.get();
        } else
            successfulCode = SuccessfulCode.ONBOARDING_REQUIRED;

        return ApiResponse.success(HttpStatus.OK,
                successfulCode,
                "Successfully determined user status",
                userDto,
                request);
    }


    @PatchMapping("/me")
    public ResponseEntity<SuccessResponseDto<UserDto>> updateUserDetails(@RequestBody UpdateUserProfileRequestDto userProfileRequestDto,
                                                                         @AuthenticationPrincipal Jwt jwt,
                                                                         HttpServletRequest request) {
        UserDto userDto = userService.updateUserDetails(userProfileRequestDto, jwt);

        return ApiResponse.success(HttpStatus.OK,
                SuccessfulCode.USER_UPDATED,
                "Updated Successfully",
                userDto,
                request);
    }
}
