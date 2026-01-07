package eu.irrationalcharm.userservice.dto.request;

import eu.irrationalcharm.validation.UsernameValid;
import eu.irrationalcharm.userservice.enums.UpdateFriendRequestStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateFriendRequestDto(
        @UsernameValid
        String username,
        @NotNull
        UpdateFriendRequestStatus action) {
}
