package eu.irrationalcharm.userservice.application.port.in;

import eu.irrationalcharm.dto.user_service.FriendRequestDto;
import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.enums.SuccessfulCode;
import eu.irrationalcharm.userservice.dto.request.UpdateFriendRequestDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ManageFriendshipUseCase {

    Set<PublicUserResponseDto> getFriends(UUID userId);

    List<FriendRequestDto> getPendingFriendRequests(UUID userId);

    void sendFriendRequest(UUID requestorId, String targetUsername);

    void removeFriend(UUID principalId, String targetUsername);

    SuccessfulCode updateFriendRequest(UUID currentUserId, Long requestId, UpdateFriendRequestDto dto);
}
