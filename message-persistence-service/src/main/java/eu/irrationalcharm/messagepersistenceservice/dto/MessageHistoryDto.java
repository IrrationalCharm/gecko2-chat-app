package eu.irrationalcharm.messagepersistenceservice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record MessageHistoryDto(
        String conversationId,
        List<MessageDto> messages,

        int pageNumber,
        int totalPages,
        boolean isLastPage
) {
}
