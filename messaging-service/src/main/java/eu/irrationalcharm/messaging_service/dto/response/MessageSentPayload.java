package eu.irrationalcharm.messaging_service.dto.response;

import eu.irrationalcharm.messaging_service.enums.MessageType;

public record MessageSentPayload(
        MessageType type,
        String messageId
) implements ServerMessage {
}
