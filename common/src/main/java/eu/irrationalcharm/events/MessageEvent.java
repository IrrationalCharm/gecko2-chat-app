package eu.irrationalcharm.events;

import eu.irrationalcharm.enums.TextType;

import java.time.LocalDateTime;

public record MessageEvent(
        String conversationId,
        String senderId,
        String recipientId,
        String content,
        LocalDateTime timestamp,
        TextType textType
) {
}
