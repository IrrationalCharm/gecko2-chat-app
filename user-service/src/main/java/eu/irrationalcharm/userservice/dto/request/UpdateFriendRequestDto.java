package eu.irrationalcharm.userservice.dto.request;

import eu.irrationalcharm.userservice.enums.FriendRequestAction;
import jakarta.validation.constraints.NotNull;

public record UpdateFriendRequestDto(
        @NotNull
        FriendRequestAction action) {
}
