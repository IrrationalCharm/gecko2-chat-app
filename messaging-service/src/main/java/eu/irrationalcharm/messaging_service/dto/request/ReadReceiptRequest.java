package eu.irrationalcharm.messaging_service.dto.request;

import eu.irrationalcharm.messaging_service.enums.MessageType;
import eu.irrationalcharm.messaging_service.validation.MessageValid;
import jakarta.validation.constraints.NotNull;

@MessageValid
public record ReadReceiptRequest(
        @NotNull
        MessageType type,
        @NotNull
        String senderId,
        @NotNull
        String recipientId

) implements ClientMessage {
}
