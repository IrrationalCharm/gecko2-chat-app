package eu.irrationalcharm.userservice.mapper;

import eu.irrationalcharm.userservice.dto.UserDto;
import eu.irrationalcharm.userservice.entity.UserEntity;
import jakarta.validation.constraints.NotNull;

public final class UserMapper {

    public static UserEntity mapToUserEntity(@NotNull UserDto userDto) {
        UserEntity userEntity = new UserEntity();

        userEntity.setUsername(userDto.username());
        userEntity.setDisplayName(userDto.displayName());
        userEntity.setEmail(userDto.email());
        userEntity.setMobileNumber(userDto.mobileNumber());
        userEntity.setProfileBio(userDto.profileBio());
        userEntity.setProfileImageUrl(userDto.profileImageUrl());

        return userEntity;
    }

    public static UserDto mapToUserDto(@NotNull UserEntity userEntity) {
        return UserDto.builder()
                .username(userEntity.getUsername())
                .displayName(userEntity.getDisplayName())
                .email(userEntity.getEmail())
                .mobileNumber(userEntity.getMobileNumber())
                .profileBio(userEntity.getProfileBio())
                .profileImageUrl(userEntity.getProfileImageUrl())
                .build();
    }

}
