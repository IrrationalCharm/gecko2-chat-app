package eu.irrationalcharm.dto.user_service;

import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record PublicUserResponseDto(
        UUID userId,
        String username,
        String displayName,
        String profileBio,
        String profileImageUrl) implements Serializable {
}
