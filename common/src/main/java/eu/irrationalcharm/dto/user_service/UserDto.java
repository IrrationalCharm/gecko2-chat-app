package eu.irrationalcharm.dto.user_service;

import eu.irrationalcharm.validation.UsernameValid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.io.Serializable;

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


        @NotBlank
        @Email(message = "This field has to be a well formed email")
        String email,

        String mobileNumber,
        String profileBio,
        String profileImageUrl) implements Serializable {
}
