package eu.irrationalcharm.messaging_service.dto.response;

import eu.irrationalcharm.messaging_service.enums.MessageType;

import java.time.Instant;

public record MessageDeliveredPayload(
        MessageType type,
        String messageId,
        String senderOfMessage,
        String recipientOfMessage,
        String timestamp
) implements ServerMessage {
}
