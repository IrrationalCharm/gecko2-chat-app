package eu.irrationalcharm.userservice.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FriendshipEntityTest {

    @Test
    @DisplayName("Test if prePersist() orders friend entities")
    void prePersist_whenUnorderedFriendshipPersists_returnsOrderedFriendEntity() {
        // Arrange
        UUID lowUuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID highUuid = UUID.fromString("00000000-0000-0000-0000-000000000002");

        var userEntityLow = new UserEntity();
        userEntityLow.setId(lowUuid);
        var userEntityHigh = new UserEntity();
        userEntityHigh.setId(highUuid);

        var friendshipEntity = new FriendshipEntity();
        friendshipEntity.setFriendB(userEntityHigh);
        friendshipEntity.setFriendA(userEntityLow);

        // Act
        friendshipEntity.prePersist();

        // Assert
        assertEquals(highUuid, friendshipEntity.getFriendA().getId());
        assertEquals(lowUuid, friendshipEntity.getFriendB().getId());

    }


    @Test
    @DisplayName("Test if prePersist() doesnt change already ordered entities")
    void prePersist_whenOrderedFriendshipPersists_doesntChangeOrder() {
        // Arrange
        UUID lowUuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID highUuid = UUID.fromString("00000000-0000-0000-0000-000000000002");

        var userEntityLow = new UserEntity();
        userEntityLow.setId(lowUuid);
        var userEntityHigh = new UserEntity();
        userEntityHigh.setId(highUuid);

        var friendshipEntity = new FriendshipEntity();
        friendshipEntity.setFriendB(userEntityLow);
        friendshipEntity.setFriendA(userEntityHigh);

        // Act
        friendshipEntity.prePersist();

        // Assert
        assertEquals(highUuid, friendshipEntity.getFriendA().getId());
        assertEquals(lowUuid, friendshipEntity.getFriendB().getId());

    }

}
