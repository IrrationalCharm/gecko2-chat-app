package eu.irrationalcharm.messaging_service.mapper;


import eu.irrationalcharm.messaging_service.dto.request.DeliveredReceiptRequest;
import eu.irrationalcharm.messaging_service.dto.request.ReadReceiptRequest;
import eu.irrationalcharm.messaging_service.dto.request.SendMessageRequest;
import eu.irrationalcharm.messaging_service.dto.response.ChatMessagePayload;
import eu.irrationalcharm.messaging_service.dto.response.MessageDeliveredPayload;
import eu.irrationalcharm.messaging_service.dto.response.MessageReadPayload;
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


    public static MessageDeliveredPayload mapToMessageDeliveredPayload(DeliveredReceiptRequest request, Instant deliveredTimestamp) {
        return new MessageDeliveredPayload(
                MessageType.MESSAGE_DELIVERED_SERVER,
                request.messageId(),
                request.senderId(),
                request.recipientId(),
                deliveredTimestamp.toString());
    }


    public static MessageReadPayload mapToMessageReadPayload(ReadReceiptRequest request, Instant readTimestamp) {
        return new MessageReadPayload(
                MessageType.MESSAGE_READ_SERVER,
                request.senderId(),
                request.recipientId(),
                readTimestamp.toString());
    }
}
