package eu.irrationalcharm.mobilebff.dto;

import eu.irrationalcharm.dto.persistence_service.ConversationSummaryDto;
import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.dto.user_service.UserDto;

import java.util.List;
import java.util.Set;

public record StartupDataDto(
        UserDto userDto, //user-service/api/v1/users
        Set<PublicUserResponseDto> friendsList, //user-service/api/v1/friends
        List<ConversationSummaryDto> conversationSummary //message-persistence-service/last-messages
) {
}
