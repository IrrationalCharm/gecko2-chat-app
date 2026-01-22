package eu.irrationalcharm.dto.persistence_service;


import java.time.Instant;
import java.util.Set;

public record ConversationSummaryDto(
        String conversationId,
        Set<String> participants,
        LastMessageDto lastMessage,
        Instant updatedAt
) {
}
