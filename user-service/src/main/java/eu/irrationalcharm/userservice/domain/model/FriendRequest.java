package eu.irrationalcharm.userservice.domain.model;

import java.time.Instant;
import java.util.UUID;

public record FriendRequest(
        Long id,
        UUID initiatorId,
        UUID receiverId,
        Instant createdAt
) {}
