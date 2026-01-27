package eu.irrationalcharm.mobilebff.dto;

import eu.irrationalcharm.dto.user_service.FriendRequestDto;
import eu.irrationalcharm.dto.persistence_service.MessageHistoryDto;
import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.dto.user_service.UserDto;

import java.util.List;
import java.util.Set;

public record StartupDataDto(
        UserDto userDto, //user-service/api/v1/users
        Set<PublicUserResponseDto> friendsList, //user-service/api/v1/friends
        List<MessageHistoryDto> conversationSummary, //message-persistence-service/last-messages
        List<FriendRequestDto> pendingRequests
) {
}
