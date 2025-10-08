package eu.irrationalcharm.userservice.dto.request;


import eu.irrationalcharm.userservice.annotation.UsernameValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record OnBoardingRequestDto(
        @UsernameValid
        String username,

        @Size(message = "Display name must be between 3 and 50 characters", min = 3, max = 20)
        @NotBlank(message = "Display Name cannot be empty")
        String displayName,

        String mobileNumber,
        String profileBio,
        String profileImageUrl
) {
}
