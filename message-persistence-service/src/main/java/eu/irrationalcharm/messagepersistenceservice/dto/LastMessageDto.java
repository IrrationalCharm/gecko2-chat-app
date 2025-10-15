package eu.irrationalcharm.messagepersistenceservice.dto;


import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LastMessageDto(
        String senderId,
        String content,
        LocalDateTime timestamp
) {
}
