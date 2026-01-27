package eu.irrationalcharm.messaging_service.dto.response;

import eu.irrationalcharm.messaging_service.enums.MessageType;

//Package to be sent through websocket
public sealed interface ServerMessage permits ChatMessagePayload, MessageSentPayload, FriendRequestPayload {
    MessageType type();

}
