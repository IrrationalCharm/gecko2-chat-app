package eu.irrationalcharm.userservice.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequestDto(
                                @Size(message = "Display name must be between 3 and 50 characters", min = 3, max = 20)
                                String displayName,

                                @Size(message = "Mobile number cannot be over 20 characters", max = 20)
                                String mobileNumber,

                                @Size(message = "Profile bio cannot be over 150 characters", max = 150)
                                String profileBio,

                                @Size(message = "Maximum is 1500 characters", max = 1500)
                                String profileImageUrl) {
}
