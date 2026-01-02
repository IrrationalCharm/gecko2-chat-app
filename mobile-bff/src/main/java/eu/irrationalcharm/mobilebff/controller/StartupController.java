package eu.irrationalcharm.mobilebff.controller;

import eu.irrationalcharm.dto.response.SuccessResponseDto;
import eu.irrationalcharm.mobilebff.dto.StartupDataDto;
import eu.irrationalcharm.mobilebff.dto.response.ApiResponse;
import eu.irrationalcharm.mobilebff.enums.SuccessfulCode;
import eu.irrationalcharm.mobilebff.service.StartupService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/startup")
@RequiredArgsConstructor
public class StartupController {


    private final StartupService startupService;

    @GetMapping
    public ResponseEntity<SuccessResponseDto<StartupDataDto>> getStartupData(HttpServletRequest request) {

        StartupDataDto startupDataDto = startupService.getStartupData();

        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.STARTUP_DATA_LOADED,
                "Startup data retrieved successfully",
                startupDataDto,
                request
        );
    }
}
