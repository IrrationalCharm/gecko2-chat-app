package eu.irrationalcharm.userservice.service;

import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.userservice.config.properties.CdnProperties;
import eu.irrationalcharm.userservice.entity.FriendshipEntity;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.userservice.repository.FriendshipRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceTest {

    @InjectMocks
    private FriendshipService friendshipService;

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private CdnProperties cdnProperties;

    private final UUID lowUuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID highUuid = UUID.fromString("00000000-0000-0000-0000-000000000002");

    // test<System Under Test>_<Condition or State Change>_<Expected Result>
    @Test
    @DisplayName("Test if areFriends runs the queries entities in correct order")
    void testAreFriends_whenGivenUnorderedEntities_shouldAddParametersToRepositoryInCorrectOrder() {
        // Arrange
        var lowerUserEntity = new UserEntity();
        var higherUserEntity = new UserEntity();
        lowerUserEntity.setId(lowUuid);
        higherUserEntity.setId(highUuid);

        when(friendshipRepository.existsByFriendAAndFriendB(higherUserEntity, lowerUserEntity)).thenReturn(true);

        // Act
        boolean areFriends = friendshipService.areFriends(lowerUserEntity, higherUserEntity);

        // Assert
        assertTrue(areFriends, "Expected true but got false");
        verify(friendshipRepository, times(1)).existsByFriendAAndFriendB(higherUserEntity, lowerUserEntity);

    }


    @Test
    @DisplayName("Test if areFriends when given entities in correct order, runs them as is")
    void testAreFriends_whenGivenOrderedEntities_shouldAddParametersToRepositoryInCorrectOrder() {
        // Arrange
        var lowerUserEntity = new UserEntity();
        var higherUserEntity = new UserEntity();
        lowerUserEntity.setId(lowUuid);
        higherUserEntity.setId(highUuid);

        when(friendshipRepository.existsByFriendAAndFriendB(higherUserEntity, lowerUserEntity)).thenReturn(true);

        // Act
        boolean areFriends = friendshipService.areFriends(higherUserEntity, lowerUserEntity);

        // Assert
        assertTrue(areFriends, "Expected true but got false");
        verify(friendshipRepository, times(1)).existsByFriendAAndFriendB(higherUserEntity, lowerUserEntity);

    }


    //:(
    @Test
    @DisplayName("Test if getFriends returns an empty Set when user has no friends =(")
    void testGetFriends_whenUserHasNoFriends_shouldReturnEmptySet() {
        // Arrange
        var userEntity = new UserEntity();
        userEntity.setId(lowUuid);

        when(friendshipRepository.findAllFriendsByUserEntity(any())).thenReturn(Collections.emptySet());

        // Act
        Set<PublicUserResponseDto> friends = friendshipService.getFriends(userEntity);

        // Assert
        assertTrue(friends.isEmpty());
    }


    @Test
    @DisplayName("Test getFriends maps entities to DTO")
    void testGetFriend_whenUserHasFriends_shouldReturnMappedDto() {
        final String friendImageUrl = "users/71a523e0-85e2-4c12-bd37-5df58f9efa05/e542f0ba-e1e6-41cc-9cd6-dbeb86f41dda_thumb.jpg";
        final String cdnBaseUrl = "abc.cloudfront.net";

        // Arrange
        var userEntity = new UserEntity();
        userEntity.setId(lowUuid);

        var friendEntity = new UserEntity();
        friendEntity.setId(highUuid);
        friendEntity.setDisplayName("Kaylin");
        friendEntity.setProfileImageUrl(friendImageUrl);
        friendEntity.setUsername("kaylinita");
        friendEntity.setProfileBio("la mas guapa");

        Set<UserEntity> friends = Set.of(friendEntity);

        when(cdnProperties.baseUrl()).thenReturn(cdnBaseUrl);
        when(friendshipRepository.findAllFriendsByUserEntity(userEntity)).thenReturn(friends);

        // Act
        Set<PublicUserResponseDto> friendsDto = friendshipService.getFriends(userEntity);

        // Assert
        assertNotNull(friendsDto);
        assertEquals(1, friendsDto.size(), ()-> "Expected one friend but received a list of " + friendsDto.size());

        var friend = friendsDto.stream().findFirst().orElseThrow();

        assertEquals(friendEntity.getId(), friend.internalId());
        assertEquals(friendEntity.getDisplayName() , friend.displayName());

        final String expectedUrl = String.format("%s/%s", cdnBaseUrl, friendImageUrl);
        assertEquals(expectedUrl , friend.profileImageUrl());
        assertEquals(friendEntity.getUsername() , friend.username());
        assertEquals(friendEntity.getProfileBio() , friend.profileBio());
    }


    @Test
    @DisplayName("Test addFriendOrThrow throws BusinessException when attempting to add himself as friend")
    void testAddFriendOrThrow_whenUserAttemptsToAddHimselfAsFriend_shouldThrowBusinessException() {
        // Arrange
        var userEntity = new UserEntity();
        userEntity.setId(highUuid);

        // Act & Assert
        assertThrows(BusinessException.class, () -> friendshipService.addFriendOrThrow(userEntity, userEntity));
    }


    @Test
    @DisplayName("Test addFriendOrThrow throws BusinessException when users are already friends")
    void testAddFriendOrThrow_whenUsersAreAlreadyFriends_shouldThrowBusinessException() {
        // Arrange
        var lowerUserEntity = new UserEntity();
        var higherUserEntity = new UserEntity();
        lowerUserEntity.setId(lowUuid);
        higherUserEntity.setId(highUuid);

        when(friendshipRepository.existsByFriendAAndFriendB(higherUserEntity, lowerUserEntity)).thenReturn(true);

        // Act & Assert
        assertThrows(BusinessException.class, () -> friendshipService.addFriendOrThrow(lowerUserEntity, higherUserEntity));
    }


    @Test
    @DisplayName("Test addFriendOrThrow throws BusinessException when users are already friends")
    void testAddFriendOrThrow_whenUserAddsFriend_shouldSaveToRepository() {
        // Arrange
        var lowerUserEntity = new UserEntity();
        var higherUserEntity = new UserEntity();
        lowerUserEntity.setId(lowUuid);
        higherUserEntity.setId(highUuid);

        when(friendshipRepository.existsByFriendAAndFriendB(higherUserEntity, lowerUserEntity)).thenReturn(false);

        // Act
        friendshipService.addFriendOrThrow(lowerUserEntity, higherUserEntity);

        // Assert
        verify(friendshipRepository, times(1)).existsByFriendAAndFriendB(higherUserEntity, lowerUserEntity);
        verify(friendshipRepository, times(1)).save(any());
    }


    @Test
    @DisplayName("Test if removeFriend correctly runs delete method")
    void testRemoveFriend_whenUserDeletesFriend_shouldReturnTrue() {
        // Arrange
        var lowerUserEntity = new UserEntity();
        var higherUserEntity = new UserEntity();
        lowerUserEntity.setId(lowUuid);
        higherUserEntity.setId(highUuid);

        var friendShipEntity = new FriendshipEntity();
        friendShipEntity.setFriendA(higherUserEntity);
        friendShipEntity.setFriendB(lowerUserEntity);

        when(friendshipRepository.findByFriendAAndFriendB(higherUserEntity, lowerUserEntity)).thenReturn(Optional.of(friendShipEntity));

        // Act
        boolean isFriendRemoved = friendshipService.removeFriend(lowerUserEntity, higherUserEntity);

        // Assert
        assertTrue(isFriendRemoved);
        verify(friendshipRepository, times(1)).findByFriendAAndFriendB(higherUserEntity, lowerUserEntity);
        verify(friendshipRepository, times(1)).delete(any());
    }
}
