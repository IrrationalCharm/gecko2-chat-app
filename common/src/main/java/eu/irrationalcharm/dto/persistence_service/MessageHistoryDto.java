package eu.irrationalcharm.dto.persistence_service;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record MessageHistoryDto(
        String conversationId,
        List<MessageDto> messages,

        Instant lastDeliveredMessage,  //The other user of the requester
        Instant lastReadMessage,        //The other user of the requester

        long unreadCount,

        int pageNumber,
        int totalPages,
        boolean isLastPage
) {
}
