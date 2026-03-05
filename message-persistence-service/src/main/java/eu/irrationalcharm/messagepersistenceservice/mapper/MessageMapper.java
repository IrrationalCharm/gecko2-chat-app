package eu.irrationalcharm.messagepersistenceservice.mapper;

import eu.irrationalcharm.dto.persistence_service.MessageDto;
import eu.irrationalcharm.enums.MessageStatus;
import eu.irrationalcharm.messagepersistenceservice.model.Message;

public final class MessageMapper {

    public static MessageDto mapToDto(Message message, MessageStatus status) {
        return new MessageDto(
                message.getClientMsgId(),
                message.getConversationId(),
                message.getSenderId(),
                message.getContent(),
                status,
                message.getTimestamp(),
                message.getTextType()
        );
    }
}
