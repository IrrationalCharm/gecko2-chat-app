package eu.irrationalcharm.dto.user_service;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record FriendRequestDto(
        long id,
        UUID initiatorId,
        UUID receiverId,

        String initiatorUsername,
        String initiatorDisplayName,
        String initiatorUrlProfileImage,

        Instant createdAt
) {
    //Jackson will convert createdAt to epoch mili
    public long getCreatedAt() {
        return createdAt != null ? createdAt.toEpochMilli() : 0;
    }
}
