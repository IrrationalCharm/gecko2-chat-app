package eu.irrationalcharm.userservice.domain.model;

import java.util.UUID;

public record User(
        UUID id,
        String providerId,
        String username,
        String displayName,
        String email,
        String mobileNumber,
        String profileBio,
        String profileImageUrl
) {}
