package eu.irrationalcharm.messagepersistenceservice.mapper;

import eu.irrationalcharm.dto.persistence_service.ConversationSummaryDto;
import eu.irrationalcharm.dto.persistence_service.LastMessageDto;
import eu.irrationalcharm.messagepersistenceservice.model.Conversation;
import eu.irrationalcharm.messagepersistenceservice.model.LastMessage;

public final class ConversationMapper {

    public static ConversationSummaryDto mapToDto(Conversation conv) {
        LastMessage lastMessage = conv.getLastMessage();

        var lastMessageDto = LastMessageDto.builder()
                .clientMsgId(lastMessage.getClientMsgId())
                .conversationId(conv.getId())
                .senderId(lastMessage.getSenderId())
                .content(lastMessage.getContent())
                .timestamp(lastMessage.getTimestamp())
                .textType(lastMessage.getTextType())
                .build();

        return new ConversationSummaryDto(
                conv.getId(),
                conv.getParticipants(),
                lastMessageDto,
                conv.getUpdatedAt()
        );
    }
}
