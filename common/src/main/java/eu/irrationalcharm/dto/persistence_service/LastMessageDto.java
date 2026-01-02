package eu.irrationalcharm.dto.persistence_service;


import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LastMessageDto(
        String senderId,
        String content,
        LocalDateTime timestamp
) {
}
