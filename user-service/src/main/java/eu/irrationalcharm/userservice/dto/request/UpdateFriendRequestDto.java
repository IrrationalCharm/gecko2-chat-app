package eu.irrationalcharm.userservice.dto.request;

import eu.irrationalcharm.userservice.enums.UpdateFriendRequestStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateFriendRequestDto(
        @NotNull
        UpdateFriendRequestStatus action) {
}
