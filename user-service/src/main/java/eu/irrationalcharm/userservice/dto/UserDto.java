package eu.irrationalcharm.userservice.dto;

import eu.irrationalcharm.userservice.annotation.UsernameValid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link eu.irrationalcharm.userservice.entity.UserEntity}
 */
@Builder
public record UserDto(

        @NotNull
        String providerId,

        @Size(message = "Display name must be between 3 and 50 characters", min = 3, max = 20)
        @NotBlank(message = "Display Name cannot be empty")
        @UsernameValid
        String username,

        @Size(message = "Display name must be between 3 and 50 characters", min = 3, max = 20)
        @NotBlank(message = "Display Name cannot be empty")
        String displayName,

        @Email(message = "This field has to be a well formed email")
        @NotBlank
        String email,

        String mobileNumber,
        String profileBio,
        String profileImageUrl) implements Serializable {
}
