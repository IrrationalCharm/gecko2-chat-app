package eu.irrationalcharm.dto.persistence_service;

import eu.irrationalcharm.enums.TextType;

import java.time.LocalDateTime;

public record MessageDto(

        String conversationId,
        String senderId,
        String content,
        LocalDateTime timestamp,
        TextType textType
) {
}
