package eu.irrationalcharm.messagepersistenceservice.mapper;

import eu.irrationalcharm.messagepersistenceservice.dto.ConversationSummaryDto;
import eu.irrationalcharm.messagepersistenceservice.dto.LastMessageDto;
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
