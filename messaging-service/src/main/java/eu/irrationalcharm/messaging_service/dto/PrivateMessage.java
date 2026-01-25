package eu.irrationalcharm.messaging_service.dto;

import eu.irrationalcharm.messaging_service.enums.PrivateMessageType;

//Package to be sent through websocket
public sealed interface PrivateMessage permits ChatMessageDto, MessageReceivedDto, FriendRequestReceivedDto {
    PrivateMessageType type();

}
