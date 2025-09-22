package eu.irrationalcharm.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link eu.irrationalcharm.userservice.entity.UserEntity}
 */
@Builder
public record UserDto(
        @Size(message = "Display name must be between 3 and 50 characters", min = 3, max = 50)
        @NotBlank(message = "Display Name cannot be empty")
        String username,

        @Size(message = "Display name must be between 3 and 50 characters", min = 3, max = 50)
        @NotBlank(message = "Display Name cannot be empty")
        String displayName,

        @Email(message = "This field has to be a well formed email")
        @NotBlank
        String email,

        String mobileNumber,
        String profileBio,
        String profileImageUrl) implements Serializable {
}