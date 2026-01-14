package eu.irrationalcharm.messagepersistenceservice.mapper;

import eu.irrationalcharm.dto.persistence_service.MessageDto;
import eu.irrationalcharm.messagepersistenceservice.model.Message;

public final class MessageMapper {

    public static MessageDto mapToDto(Message message) {
        return new MessageDto(
                message.getClientMsgId(),
                message.getConversationId(),
                message.getSenderId(),
                message.getContent(),
                message.getTimestamp(),
                message.getTextType()
        );
    }
}
