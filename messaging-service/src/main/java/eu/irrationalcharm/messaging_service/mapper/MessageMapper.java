package eu.irrationalcharm.messaging_service.mapper;


import eu.irrationalcharm.kafka.enums.TextType;
import eu.irrationalcharm.kafka.events.MessageEvent;
import eu.irrationalcharm.messaging_service.dto.ChatMessageDto;

import java.time.LocalDateTime;

public final class MessageMapper {

    public static MessageEvent mapToMessageEvent(ChatMessageDto messageDto) {
        String conversationId = generateConversationId(messageDto.userId(), messageDto.recipientId());
        return new MessageEvent(
                conversationId,
                messageDto.userId(),
                messageDto.recipientId(),
                messageDto.content(),
                LocalDateTime.now(),
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
