package eu.irrationalcharm.userservice.controller;

import eu.irrationalcharm.dto.user_service.UserDto;
import eu.irrationalcharm.dto.response.SuccessResponseDto;
import eu.irrationalcharm.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.annotation.UsernameValid;
import eu.irrationalcharm.userservice.dto.request.OnBoardingRequestDto;
import eu.irrationalcharm.userservice.dto.response.base.ApiResponse;
import eu.irrationalcharm.userservice.service.orchestrator.RegistrationOrchestrator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/register")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationOrchestrator registrationOrchestrator;

    @PostMapping(path = "/onboard")
    public ResponseEntity<SuccessResponseDto<UserDto>> completeOnBoarding(
                                                                        @RequestBody @Valid OnBoardingRequestDto boardingResponseDto,
                                                                        @AuthenticationPrincipal Jwt jwt,
                                                                        HttpServletRequest request) {

        UserDto userDto = registrationOrchestrator.onBoarding(boardingResponseDto, jwt);

        return ApiResponse.success(
                HttpStatus.CREATED,
                SuccessfulCode.USER_CREATED,
                "User has been successfully created",
                userDto,
                request
        );
    }


    @GetMapping("/username-availability")
    public ResponseEntity<SuccessResponseDto<String>> checkUsernameAvailability(
                                                                        @RequestParam @UsernameValid String username,
                                                                        HttpServletRequest request) {

        boolean isTaken = registrationOrchestrator.checkUsernameAvailability(username);

        SuccessfulCode code = isTaken ? SuccessfulCode.USERNAME_TAKEN : SuccessfulCode.USERNAME_AVAILABLE;
        String detail = isTaken ? "Username is taken" : "Username is available";

        return ApiResponse.success(
                HttpStatus.OK,
                code,
                detail,
                username,
                request
        );
    }
}
