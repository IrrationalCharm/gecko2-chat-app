package eu.irrationalcharm.messagepersistenceservice.mapper;

import eu.irrationalcharm.dto.persistence_service.ConversationSummaryDto;
import eu.irrationalcharm.dto.persistence_service.LastMessageDto;
import eu.irrationalcharm.messagepersistenceservice.model.Conversation;
import eu.irrationalcharm.messagepersistenceservice.model.LastMessage;
import org.springframework.lang.NonNull;

public final class ConversationMapper {

    public static ConversationSummaryDto mapToDto(@NonNull Conversation conv) {
        LastMessage lastMessage = conv.getLastMessage();
        LastMessageDto lastMessageDto = new LastMessageDto(lastMessage.getSenderId(), lastMessage.getContent(), lastMessage.getTimestamp());

        return new ConversationSummaryDto(
                conv.getId(),
                conv.getParticipants(),
                lastMessageDto,
                conv.getUpdatedAt()
        );
    }
}
