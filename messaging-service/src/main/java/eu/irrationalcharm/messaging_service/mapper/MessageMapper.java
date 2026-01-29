package eu.irrationalcharm.messaging_service.mapper;


import eu.irrationalcharm.messaging_service.dto.request.DeliveredReceiptRequest;
import eu.irrationalcharm.messaging_service.dto.request.SendMessageRequest;
import eu.irrationalcharm.messaging_service.dto.response.ChatMessagePayload;
import eu.irrationalcharm.messaging_service.dto.response.MessageDeliveredPayload;
import eu.irrationalcharm.messaging_service.enums.MessageType;

import java.time.Instant;

public final class MessageMapper {

    public static ChatMessagePayload mapToChatMessagePayload(SendMessageRequest msg, Instant timestamp) {
        return new ChatMessagePayload(
                MessageType.CHAT_MESSAGE_SERVER,
                msg.clientMsgId(),
                msg.senderId(),
                msg.recipientId(),
                msg.textType(),
                msg.content(),
                timestamp.toString());
    }


    public static MessageDeliveredPayload mapToMessageDeliveredPayload(DeliveredReceiptRequest msg, Instant timestamp) {
        return new MessageDeliveredPayload(
                MessageType.MESSAGE_DELIVERED_SERVER,
                msg.messageId(),
                msg.senderId(),
                msg.recipientId(),
                timestamp.toString());
    }

    private static String generateConversationId(String senderId, String recipientId) {
        if(senderId.compareTo(recipientId) > 0) {
            return String.format("%s:%s", senderId, recipientId);
        } else {
            return String.format("%s:%s", recipientId, senderId);
        }
    }
}
