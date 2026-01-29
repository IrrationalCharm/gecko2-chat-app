package eu.irrationalcharm.messaging_service.dto.request;

import eu.irrationalcharm.messaging_service.enums.MessageType;
import eu.irrationalcharm.messaging_service.validation.MessageValid;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@MessageValid
public record DeliveredReceiptRequest(
        @NotNull
        MessageType type,
        @NotNull
        String senderId,
        @NotNull
        String recipientId,
        @NotNull
        String messageId,
        @NotNull
        String conversationId,
        @NotNull
        String timestamp

) implements ClientMessage {
}
