package eu.irrationalcharm.messaging_service.dto.response;

import eu.irrationalcharm.messaging_service.enums.MessageType;

public record MessageReadPayload(
        MessageType type,
        String senderOfMessage,
        String recipientOfMessage,
        String timestamp
) implements ServerMessage {
}
