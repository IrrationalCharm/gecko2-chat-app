package eu.irrationalcharm.messaging_service.dto.request;

import eu.irrationalcharm.messaging_service.enums.MessageType;
import eu.irrationalcharm.messaging_service.validation.MessageValid;
import jakarta.validation.constraints.NotNull;

@MessageValid
public record ReadReceiptRequest(
        @NotNull
        MessageType type,
        @NotNull
        String senderId, //Sender of reques
        @NotNull
        String recipientId, //Recipient of request
        @NotNull
        String messageId,
        @NotNull
        String conversationId,
        @NotNull
        String readTimestamp

) implements ClientMessage {
}
