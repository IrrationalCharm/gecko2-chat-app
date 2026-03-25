package eu.irrationalcharm.userservice.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Friendship(
        Long id,
        UUID friendAId,
        UUID friendBId,
        Instant createdAt
) {}
