package eu.irrationalcharm.messaging_service.dto;

import eu.irrationalcharm.messaging_service.enums.PrivateMessageType;

public record MessageReceivedDto(
        PrivateMessageType type,
        String uuid
) implements PrivateMessage {
}
