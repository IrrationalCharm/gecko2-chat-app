package eu.irrationalcharm.messaging_service.mapper;


import eu.irrationalcharm.enums.TextType;
import eu.irrationalcharm.events.MessageEvent;
import eu.irrationalcharm.messaging_service.dto.ChatMessageDto;

import java.time.Instant;

public final class MessageMapper {

    public static MessageEvent mapToMessageEvent(ChatMessageDto messageDto) {
        String conversationId = generateConversationId(messageDto.senderId(), messageDto.recipientId());
        return new MessageEvent(
                messageDto.clientMsgId(),
                conversationId,
                messageDto.senderId(),
                messageDto.recipientId(),
                messageDto.content(),
                Instant.now(), //TODO fix this?
                TextType.TEXT
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
