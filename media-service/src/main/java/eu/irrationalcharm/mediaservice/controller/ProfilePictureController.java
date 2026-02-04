package eu.irrationalcharm.mediaservice.controller;


import eu.irrationalcharm.dto.response.SuccessResponseDto;
import eu.irrationalcharm.enums.SuccessfulCode;
import eu.irrationalcharm.mediaservice.dto.response.ApiResponse;
import eu.irrationalcharm.mediaservice.service.ProfilePictureService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProfilePictureController {

    private final ProfilePictureService profilePictureService;

    @PutMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponseDto<String>> uploadProfileImage(@RequestPart MultipartFile image,
                                                                       @AuthenticationPrincipal Jwt jwt,
                                                                       HttpServletRequest request){
        if (image != null)
            log.info("Image received: {}", image.getOriginalFilename());

        String imageUrl = profilePictureService.uploadProfileImage(image, jwt);

        return ApiResponse.success(HttpStatus.OK,
                SuccessfulCode.USER_PROFILE_IMAGE_UPLOADED,
                "Updated Successfully",
                imageUrl,
                request);
    }
}
