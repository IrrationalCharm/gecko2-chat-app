package eu.irrationalcharm.messaging_service.dto.response;

import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.messaging_service.enums.MessageType;

public record FriendRequestAcceptedPayload(
        MessageType type,
        PublicUserResponseDto newFriend,
        long createdAt

) implements ServerMessage { }
