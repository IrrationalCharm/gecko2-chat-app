package eu.irrationalcharm.messaging_service.dto;

import eu.irrationalcharm.messaging_service.enums.PrivateMessageType;

import java.time.Instant;

public record FriendRequestReceivedDto(
        PrivateMessageType type,
        String senderId,
        String senderUsername,
        String senderProfileImageUrl,
        Instant createdAt

) implements PrivateMessage { }
