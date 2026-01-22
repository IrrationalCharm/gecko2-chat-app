package eu.irrationalcharm.dto.persistence_service;

import eu.irrationalcharm.enums.TextType;

import java.time.Instant;

public record MessageDto(

        String clientMsgId,
        String conversationId,
        String senderId,
        String content,
        Instant timestamp,
        TextType textType
) {
}
