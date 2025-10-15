package eu.irrationalcharm.messagepersistenceservice.mapper;

import eu.irrationalcharm.messagepersistenceservice.dto.MessageDto;
import eu.irrationalcharm.messagepersistenceservice.model.Message;

public final class MessageMapper {

    public static MessageDto mapToDto(Message message) {
        return new MessageDto(
                message.getConversationId(),
                message.getSenderId(),
                message.getContent(),
                message.getTimestamp(),
                message.getTextType()
        );
    }
}
