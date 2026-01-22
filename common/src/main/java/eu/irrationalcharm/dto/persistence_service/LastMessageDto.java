package eu.irrationalcharm.dto.persistence_service;


import eu.irrationalcharm.enums.TextType;
import lombok.Builder;

import java.time.Instant;

@Builder
public record LastMessageDto(
        String clientMsgId,
        String conversationId,
        String senderId,
        String content,
        Instant timestamp,
        TextType textType
) {
}
