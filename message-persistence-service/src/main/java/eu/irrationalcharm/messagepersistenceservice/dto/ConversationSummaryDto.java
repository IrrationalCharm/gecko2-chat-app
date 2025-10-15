package eu.irrationalcharm.messagepersistenceservice.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record ConversationSummaryDto(
        String conversationId,
        Set<String> participants,
        LastMessageDto lastMessage,
        LocalDateTime updatedAt
) {
}
