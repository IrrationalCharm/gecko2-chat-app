package eu.irrationalcharm.userservice.domain.model;

import java.util.UUID;

public record FriendshipPreference(
        Long id,
        UUID userId,
        UUID friendId,
        boolean blocked,
        boolean muted,
        boolean pinned
) {
    public FriendshipPreference withBlocked(boolean blocked) {
        return new FriendshipPreference(id, userId, friendId, blocked, muted, pinned);
    }

    public FriendshipPreference withMuted(boolean muted) {
        return new FriendshipPreference(id, userId, friendId, blocked, muted, pinned);
    }

    public FriendshipPreference withPinned(boolean pinned) {
        return new FriendshipPreference(id, userId, friendId, blocked, muted, pinned);
    }
}
