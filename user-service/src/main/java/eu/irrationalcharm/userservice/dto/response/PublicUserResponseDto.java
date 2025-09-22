package eu.irrationalcharm.userservice.dto.response;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record PublicUserResponseDto(
        String username,
        String displayName,
        String profileBio,
        String profileImageUrl) implements Serializable {
}
