package eu.irrationalcharm.messaging_service.dto.request;

import eu.irrationalcharm.messaging_service.enums.MessageType;
import jakarta.validation.constraints.NotNull;

public record TypingStatusRequest(
        @NotNull
        MessageType type,
        @NotNull
        String senderId,
        @NotNull
        String recipientId

) implements ClientMessage {
}
