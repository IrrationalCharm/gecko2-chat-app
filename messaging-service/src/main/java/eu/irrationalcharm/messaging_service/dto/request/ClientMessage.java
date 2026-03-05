package eu.irrationalcharm.messaging_service.dto.request;

import eu.irrationalcharm.messaging_service.enums.MessageType;

public sealed interface ClientMessage permits SendMessageRequest, ReadReceiptRequest, DeliveredReceiptRequest, TypingStatusRequest {
    MessageType type();
    String recipientId();
    String senderId();
}
