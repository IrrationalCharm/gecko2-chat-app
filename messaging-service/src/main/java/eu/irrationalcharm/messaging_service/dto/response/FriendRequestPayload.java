package eu.irrationalcharm.messaging_service.dto.response;

import eu.irrationalcharm.messaging_service.enums.MessageType;

public record FriendRequestPayload(
        MessageType type,
        String friendRequestId,
        String senderId,
        String senderUsername,
        String senderDisplayName,
        String senderProfileImageUrl,
        long createdAt

) implements ServerMessage { }
