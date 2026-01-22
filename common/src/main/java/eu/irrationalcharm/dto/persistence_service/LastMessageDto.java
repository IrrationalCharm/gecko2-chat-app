package eu.irrationalcharm.dto.persistence_service;


import lombok.Builder;

import java.time.Instant;

@Builder
public record LastMessageDto(
        String senderId,
        String content,
        Instant timestamp
) {
}
