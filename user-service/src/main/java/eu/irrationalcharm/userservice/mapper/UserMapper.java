package eu.irrationalcharm.userservice.mapper;

import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.dto.user_service.UserDto;
import eu.irrationalcharm.userservice.entity.UserEntity;
import jakarta.validation.constraints.NotNull;

public final class UserMapper {


    public static UserEntity mapToUserEntity(@NotNull UserDto userDto) {
        UserEntity userEntity = new UserEntity();

        userEntity.setUsername(userDto.username());
        userEntity.setProviderId(userDto.providerId());
        userEntity.setDisplayName(userDto.displayName());
        userEntity.setEmail(userDto.email());
        userEntity.setMobileNumber(userDto.mobileNumber());
        userEntity.setProfileBio(userDto.profileBio());
        userEntity.setProfileImageUrl(userDto.profileImageUrl());

        return userEntity;
    }

    public static UserDto mapToUserDto(@NotNull UserEntity userEntity, String baseCndUrl) {
        return UserDto.builder()
                .internalId(userEntity.getId().toString())
                .providerId(userEntity.getProviderId())
                .username(userEntity.getUsername())
                .displayName(userEntity.getDisplayName())
                .email(userEntity.getEmail())
                .mobileNumber(userEntity.getMobileNumber())
                .profileBio(userEntity.getProfileBio())
                .profileImageUrl(String.format("%s/%s",baseCndUrl, userEntity.getProfileImageUrl()))
                .build();
    }

    public static PublicUserResponseDto mapToPublicUserDto(@NotNull UserEntity userEntity, String baseCndUrl) {
        return PublicUserResponseDto.builder()
                .internalId(userEntity.getId())
                .username(userEntity.getUsername())
                .displayName(userEntity.getDisplayName())
                .profileBio(userEntity.getProfileBio())
                .profileImageUrl(String.format("%s/%s",baseCndUrl, userEntity.getProfileImageUrl()))
                .build();
    }

}
