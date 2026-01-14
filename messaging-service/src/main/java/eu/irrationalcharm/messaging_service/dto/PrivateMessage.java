package eu.irrationalcharm.messaging_service.dto;

import eu.irrationalcharm.messaging_service.enums.PrivateMessageType;

public sealed interface PrivateMessage permits ChatMessageDto, MessageReceivedDto{
    PrivateMessageType type();
}
