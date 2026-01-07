package eu.irrationalcharm.userservice.dto.request;


import eu.irrationalcharm.validation.UsernameValid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record OnBoardingRequestDto(
        @UsernameValid
        String username,

        @Size(message = "Display name must be between 3 and 20 characters", min = 3, max = 20)
        @NotBlank(message = "Display Name cannot be empty")
        String displayName,

        @Size(message = "Message can only be up to 20 characters", max = 20)
        String mobileNumber,

        @Size(max = 100, message = "Profile bio cannot be over 100 characters")
        String profileBio,

        @Size(max = 1000, message = "Image URL cannot be over 1000 characters")
        String profileImageUrl
) {
}
