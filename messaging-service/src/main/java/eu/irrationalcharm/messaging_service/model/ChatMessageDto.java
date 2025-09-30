package eu.irrationalcharm.messaging_service.model;


public record ChatMessageDto(
        String senderUsername,
        String recipientUsername,
        String content,
        String timestamp
) {

}
