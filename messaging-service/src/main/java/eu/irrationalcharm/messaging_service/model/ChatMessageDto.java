package eu.irrationalcharm.messaging_service.model;

import java.time.Instant;

public record ChatMessageDto(
        String senderUsername,
        String recipientUsername,
        String content,
        String timestamp
) {

}
