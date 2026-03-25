package eu.irrationalcharm.userservice.adapter.in.web;

import eu.irrationalcharm.dto.response.SuccessResponseDto;
import eu.irrationalcharm.dto.user_service.UserDto;
import eu.irrationalcharm.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.application.port.in.RegisterUserUseCase;
import eu.irrationalcharm.userservice.constants.JwtClaims;
import eu.irrationalcharm.userservice.dto.request.OnBoardingRequestDto;
import eu.irrationalcharm.userservice.dto.response.base.ApiResponse;
import eu.irrationalcharm.validation.UsernameValid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
@RequestMapping("/api/register")
@AllArgsConstructor
public class RegistrationController {

    private final RegisterUserUseCase registerUserUseCase;

    @PostMapping(path = "/onboard")
    public ResponseEntity<SuccessResponseDto<UserDto>> completeOnBoarding(
            @RequestBody @Valid OnBoardingRequestDto boardingResponseDto,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest request) {
        log.info("Request received to complete onboarding with username: {}", boardingResponseDto.username());

        String providerId = jwt.getClaimAsString(JwtClaims.SUBJECT);
        String email = jwt.getClaimAsString(JwtClaims.EMAIL);

        UserDto userDto = registerUserUseCase.onBoard(providerId, email, boardingResponseDto);

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

        boolean isTaken = registerUserUseCase.checkUsernameAvailability(username);
        SuccessfulCode code = isTaken ? SuccessfulCode.USERNAME_TAKEN : SuccessfulCode.USERNAME_AVAILABLE;
        String detail = isTaken ? "Username is taken" : "Username is available";

        return ApiResponse.success(HttpStatus.OK, code, detail, username, request);
    }
}
