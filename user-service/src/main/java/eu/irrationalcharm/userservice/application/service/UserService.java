package eu.irrationalcharm.userservice.application.service;

import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.dto.user_service.UserDto;
import eu.irrationalcharm.enums.ErrorCode;
import eu.irrationalcharm.userservice.application.port.in.GetUserUseCase;
import eu.irrationalcharm.userservice.application.port.in.UpdateUserUseCase;
import eu.irrationalcharm.userservice.application.port.out.UserRepositoryPort;
import eu.irrationalcharm.userservice.config.properties.CdnProperties;
import eu.irrationalcharm.userservice.domain.model.User;
import eu.irrationalcharm.userservice.dto.request.UpdateUserProfileRequestDto;
import eu.irrationalcharm.userservice.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserService implements GetUserUseCase, UpdateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final CdnProperties cdnProperties;

    @Override
    @Transactional(readOnly = true)
    public PublicUserResponseDto getPublicProfile(String username) {
        User user = getUserByUsernameOrThrow(username);

        return PublicUserResponseDto.builder()
                .username(user.username())
                .displayName(user.displayName())
                .profileBio(user.profileBio())
                .profileImageUrl(user.profileImageUrl())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> getAuthenticatedUserDto(UUID userId) {
        if (userId == null)
            return Optional.empty();

        return userRepositoryPort.findById(userId).map(user -> mapToUserDto(user, cdnProperties.baseUrl()));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserOrThrow(UUID userId) {
        return userRepositoryPort.findById(userId).orElseThrow(() -> {
            log.warn("Could not find account for user {}", userId);
            return new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.ON_BOARDING_REQUIRED,
                    String.format("Could not find account with this user id: %s", userId));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUsernameOrThrow(String username) {
        return userRepositoryPort.findByUsername(username).orElseThrow(() -> {
            log.warn("Could not find user with username: {}", username);
            return new BusinessException(
                    HttpStatus.NOT_FOUND,
                    ErrorCode.USERNAME_NOT_FOUND,
                    String.format("User with username %s not found.", username));
        });
    }

    @Override
    @Transactional
    public UserDto updateUserDetails(UUID userId, UpdateUserProfileRequestDto dto) {
        User user = getUserOrThrow(userId);

        User updated = new User(
                user.id(),
                user.providerId(),
                user.username(),
                dto.displayName() != null ? dto.displayName() : user.displayName(),
                user.email(),
                dto.mobileNumber() != null ? dto.mobileNumber() : user.mobileNumber(),
                dto.profileBio() != null ? dto.profileBio() : user.profileBio(),
                dto.profileImageUrl() != null ? dto.profileImageUrl() : user.profileImageUrl()
        );

        User saved = userRepositoryPort.save(updated);
        log.debug("User {} details has been updated", saved.id());
        return mapToUserDto(saved, cdnProperties.baseUrl());
    }

    @Override
    @Transactional
    public void updateProfileImageUrl(UUID userId, String profileImageUrl) {
        User user = getUserOrThrow(userId);

        User updated = new User(
                user.id(),
                user.providerId(),
                user.username(),
                user.displayName(),
                user.email(),
                user.mobileNumber(),
                user.profileBio(),
                profileImageUrl
        );

        userRepositoryPort.save(updated);
        log.debug("User {} updated user profile Url", userId);
    }

    public static UserDto mapToUserDto(User user, String cdnBaseUrl) {
        return UserDto.builder()
                .internalId(user.id().toString())
                .providerId(user.providerId())
                .username(user.username())
                .displayName(user.displayName())
                .email(user.email())
                .mobileNumber(user.mobileNumber())
                .profileBio(user.profileBio())
                .profileImageUrl(String.format("%s/%s", cdnBaseUrl, user.profileImageUrl()))
                .build();
    }

    public static PublicUserResponseDto mapToPublicUserDto(User user, String cdnBaseUrl) {
        return PublicUserResponseDto.builder()
                .internalId(user.id())
                .username(user.username())
                .displayName(user.displayName())
                .profileBio(user.profileBio())
                .profileImageUrl(String.format("%s/%s", cdnBaseUrl, user.profileImageUrl()))
                .build();
    }
}
