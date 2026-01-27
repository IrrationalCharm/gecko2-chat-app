package eu.irrationalcharm.messaging_service.mapper;


import eu.irrationalcharm.enums.TextType;
import eu.irrationalcharm.events.MessageEvent;
import eu.irrationalcharm.messaging_service.dto.request.SendMessageRequest;
import eu.irrationalcharm.messaging_service.dto.response.ChatMessagePayload;

import java.time.Instant;

public final class MessageMapper {

    public static MessageEvent mapToMessageEvent(SendMessageRequest messageRequest) {
        String conversationId = generateConversationId(messageRequest.senderId(), messageRequest.recipientId());
        return new MessageEvent(
                messageRequest.clientMsgId(),
                conversationId,
                messageRequest.senderId(),
                messageRequest.recipientId(),
                messageRequest.content(),
                Instant.now(), //TODO fix this?
                TextType.TEXT
        );
    }

    public static ChatMessagePayload mapToChatMessagePayload(SendMessageRequest msg) {
        return new ChatMessagePayload(
                msg.type(),
                msg.clientMsgId(),
                msg.senderId(),
                msg.recipientId(),
                msg.textType(),
                msg.content(),
                msg.timestamp());
    }

    private static String generateConversationId(String senderId, String recipientId) {
        if(senderId.compareTo(recipientId) > 0) {
            return String.format("%s:%s", senderId, recipientId);
        } else {
            return String.format("%s:%s", recipientId, senderId);
        }
    }
}
