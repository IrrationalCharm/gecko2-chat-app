package eu.irrationalcharm.userservice.mapper;

import eu.irrationalcharm.dto.user_service.FriendRequestDto;
import eu.irrationalcharm.userservice.entity.FriendRequestEntity;

public final class FriendRequestMapper {

    public static FriendRequestDto mapEntityToDto(FriendRequestEntity entity, String baseCndUrl) {
        return new FriendRequestDto(
                entity.getId(),
                entity.getInitiator().getId(),
                entity.getReceiver().getId(),
                entity.getInitiator().getUsername(),
                entity.getInitiator().getDisplayName(),
                String.format("%s/%s",baseCndUrl, entity.getInitiator().getProfileImageUrl()),
                entity.getCreated_at()
        );
    }
}
