package eu.irrationalcharm.userservice.service;

import eu.irrationalcharm.userservice.constants.JwtClaims;
import eu.irrationalcharm.userservice.dto.request.UpdateUserProfileRequestDto;
import eu.irrationalcharm.userservice.dto.response.PublicUserResponseDto;
import eu.irrationalcharm.userservice.dto.UserDto;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.userservice.enums.ErrorCode;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.userservice.mapper.UserMapper;
import eu.irrationalcharm.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    @Transactional(readOnly = true)
    public PublicUserResponseDto fetchPublicProfile(String username) {
        UserEntity userEntity = getEntityByUsernameOrThrow(username);

        return PublicUserResponseDto.builder()
                .username(userEntity.getUsername())
                .displayName(userEntity.getDisplayName())
                .profileBio(userEntity.getProfileBio())
                .profileImageUrl(userEntity.getProfileImageUrl())
                .build();
    }


    @Transactional(readOnly = true)
    public Optional<UserDto> getAuthenticatedDto(Jwt authJwt) {
        Optional<UserEntity> optionalUser = getAuthenticatedEntity(authJwt);

        return optionalUser.map(UserMapper::mapToUserDto);
    }


    @Transactional(readOnly = true)
    public UserEntity getAuthenticatedEntityOrThrow(Jwt authJwt) {
        String userId = authJwt.getClaimAsString(JwtClaims.INTERNAL_ID);
        if(userId == null)
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.ON_BOARDING_REQUIRED, "Empty internal_id in claim");

        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.BAD_REQUEST,
                        ErrorCode.ON_BOARDING_REQUIRED,
                        String.format("Could not find account with this user id: %s", userId)));
    }


    @Transactional(readOnly = true)
    public Optional<UserEntity> getAuthenticatedEntity(Jwt authJwt) {
        String userId = authJwt.getClaimAsString(JwtClaims.INTERNAL_ID);
        if(userId == null)
            return Optional.empty();

        return userRepository.findById(UUID.fromString(userId));
    }


    @Transactional(readOnly = true)
    public UserEntity getEntityByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new BusinessException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.USERNAME_NOT_FOUND,
                        String.format("User with username %s not found.", username)));
    }


    @Transactional(readOnly = true)
    public Optional<UserEntity> getEntityByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    @Transactional
    @PreAuthorize("isAuthenticated()")
    public UserDto updateUserDetails(UpdateUserProfileRequestDto userProfileRequestDto, Jwt jwt) {
        UserEntity userEntity = getAuthenticatedEntityOrThrow(jwt);

        if (userProfileRequestDto.mobileNumber() != null)
            userEntity.setMobileNumber(userProfileRequestDto.mobileNumber());

        if (userProfileRequestDto.profileBio() != null)
            userEntity.setProfileBio(userProfileRequestDto.profileBio());

        if(userProfileRequestDto.displayName() != null)
            userEntity.setDisplayName(userProfileRequestDto.displayName());

        if (userProfileRequestDto.profileImageUrl() != null)
            userEntity.setProfileImageUrl(userProfileRequestDto.profileImageUrl());

        return UserMapper.mapToUserDto( userRepository.save(userEntity) );
    }


}
