package eu.irrationalcharm.userservice.controller;

import eu.irrationalcharm.userservice.dto.request.OnBoardingRequestDto;
import eu.irrationalcharm.userservice.dto.UserDto;
import eu.irrationalcharm.userservice.dto.response.base.ApiResponse;
import eu.irrationalcharm.userservice.dto.response.base.SuccessResponseDto;
import eu.irrationalcharm.userservice.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.service.orchestrator.RegistrationOrchestrator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/register")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationOrchestrator registrationOrchestrator;

    @PostMapping("/onboard")
    public ResponseEntity<SuccessResponseDto<UserDto>> completeOnBoarding(
            @RequestBody @Valid OnBoardingRequestDto boardingResponseDto, @AuthenticationPrincipal Jwt jwt, HttpServletRequest request) {

        UserDto userDto = registrationOrchestrator.onBoarding(boardingResponseDto, jwt);

        return ApiResponse.success(
                HttpStatus.CREATED,
                SuccessfulCode.USER_CREATED,
                "User has been successfully created",
                userDto,
                request
        );
    }

}
