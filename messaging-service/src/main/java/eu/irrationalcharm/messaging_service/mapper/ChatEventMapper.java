package eu.irrationalcharm.messaging_service.mapper;

import eu.irrationalcharm.enums.TextType;
import eu.irrationalcharm.events.chat.MessageEvent;
import eu.irrationalcharm.events.chat.MsgDeliveredEvent;
import eu.irrationalcharm.messaging_service.dto.request.DeliveredReceiptRequest;
import eu.irrationalcharm.messaging_service.dto.request.SendMessageRequest;

import java.time.Instant;

public class ChatEventMapper {

    public static MessageEvent toMessageEvent(SendMessageRequest request, Instant timestamp) {
        String conversationId = generateConversationId(request.senderId(), request.recipientId());
        return new MessageEvent(
                request.clientMsgId(),
                conversationId,
                request.senderId(),
                request.recipientId(),
                request.content(),
                timestamp,
                TextType.TEXT
        );
    }


    public static MsgDeliveredEvent toMsgDeliveredEvent(DeliveredReceiptRequest request, Instant timestamp) {
        return new MsgDeliveredEvent(
                request.conversationId(),
                request.recipientId(),
                timestamp
        );
    }





    private static String generateConversationId(String senderId, String recipientId) {
        if(senderId.compareTo(recipientId) > 0) {
            return String.format("%s:%s", senderId, recipientId);
        } else {
            return String.format("%s:%s", recipientId, senderId);
        }
    }
}
