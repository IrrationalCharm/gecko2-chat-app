package eu.irrationalcharm.userservice.repository;

import eu.irrationalcharm.userservice.entity.FriendshipEntity;
import eu.irrationalcharm.userservice.entity.UserEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Testcontainers
class FriendshipRepositoryTest {

    @Autowired
    FriendshipRepository friendshipRepository;

    @Autowired
    TestEntityManager entityManager;

    UserEntity user1;
    UserEntity user2;
    UserEntity user3;

    FriendshipEntity friendship1_2;
    FriendshipEntity friendship1_3;

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:latest");

    @BeforeEach
    void setUp() {
        user1 = new UserEntity();
        user1.setProviderId(UUID.randomUUID().toString());
        user1.setUsername("irrational_charm");
        user1.setDisplayName("Irrational Charm");
        user1.setEmail("example@gmail.com");

        user2 = new UserEntity();
        user2.setProviderId(UUID.randomUUID().toString());
        user2.setUsername("MatiusYoky");
        user2.setDisplayName("Matius");
        user2.setEmail("matius@gmail.com");

        user3 = new UserEntity();
        user3.setProviderId(UUID.randomUUID().toString());
        user3.setUsername("kaylinita");
        user3.setDisplayName("Kaylin");
        user3.setEmail("kaylin@gmail.com");

        entityManager.persist(user2);
        entityManager.persist(user1);
        entityManager.persist(user3);
        entityManager.flush();

        friendship1_2 = new FriendshipEntity();
        friendship1_2.setFriendA(user1);
        friendship1_2.setFriendB(user2);

        friendship1_3 = new FriendshipEntity();
        friendship1_3.setFriendA(user1);
        friendship1_3.setFriendB(user3);

        entityManager.persistAndFlush(friendship1_2);
        entityManager.persistAndFlush(friendship1_3);
    }

    @Test
    @DisplayName("Test if PostgreSQL container is running")
    void isTestContainerRunning() {
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    @DisplayName("Test existsByFriendAAndFriendB should return requested true")
    void testFindByFriendAAndFriendB_whenRequestedFriendship_shouldReturnTrue() {
        // Arrange
        boolean isFriends;

        // Act
        isFriends = friendshipRepository.existsByFriendAAndFriendB(friendship1_2.getFriendA(), friendship1_2.getFriendB());

        // Assert
        assertThat(isFriends).isTrue();
    }

    @Test
    @DisplayName("Test existsByFriendAAndFriendB by requesting a friendship that doesnt exist, should return false")
    void testFindByFriendAAndFriendB_whenRequestedFriendshipDoesntExist_shouldReturnFalse() {
        // Arrange
        boolean isFriends;

        // Act
        isFriends = friendshipRepository.existsByFriendAAndFriendB(user3, user2);

        // Assert
        assertThat(isFriends).isFalse();
    }

    @Test
    @DisplayName("Test findByFriendAAndFriendB should return requested friendship")
    void testFindByFriendAAndFriendB_whenRequestedFriendship_shouldReturnPopulatedFriendshipEntity() {
        // Arrange
        Optional<FriendshipEntity> optionalFriendship;

        // Act
        optionalFriendship = friendshipRepository.findByFriendAAndFriendB(friendship1_2.getFriendA(), friendship1_2.getFriendB());

        // Assert
        assertThat(optionalFriendship.isPresent()).isTrue();
        FriendshipEntity friendship = optionalFriendship.get();

        assertThat(friendship.getId()).isEqualTo(friendship1_2.getId());
        assertThat(friendship.getFriendA().getId())
                .isEqualTo(friendship1_2.getFriendA().getId());

        assertThat(friendship.getFriendB().getId())
                .isEqualTo(friendship1_2.getFriendB().getId());
    }


    @Test
    @DisplayName("Test findByFriendAAndFriendB by sending a request of a friendship which doesnt exist")
    void testFindByFriendAAndFriendB_whenRequestedFriendshipDoesntExist_shouldReturnEmptyOptional() {
        // Arrange
        Optional<FriendshipEntity> optionalFriendship;

        // Act
        optionalFriendship = friendshipRepository.findByFriendAAndFriendB(user2, user3);

        // Assert
        assertThat(optionalFriendship.isEmpty()).isTrue();
    }


    @Test
    @DisplayName("Test findByFriendAAndFriendB by sending the request in the wrong order, should return empty optional")
    void testFindByFriendAAndFriendB_whenRequestedByWrongEntityOrder_shouldReturnEmptyOptional() {
        // Arrange
        Optional<FriendshipEntity> optionalFriendship;

        // Act
        optionalFriendship = friendshipRepository.findByFriendAAndFriendB(friendship1_3.getFriendB(), friendship1_3.getFriendA());

        // Assert
        assertThat(optionalFriendship.isEmpty()).isTrue();
    }


    @Test
    @DisplayName("Test findAllFriendsByUserEntity by requesting a list of all friends of user")
    void testFindAllFriendsByUserEntity_whenFriendsRequestedFromAUser_shouldReturnPopulatedSetOfFriends() {
        // Arrange
        Set<UserEntity> userEntities;
        int expectedFriends = 2;

        // Act
        userEntities = friendshipRepository.findAllFriendsByUserEntity(user1);

        // Assert
        assertThat(userEntities).hasSize(expectedFriends);
        assertThat(userEntities).containsOnly(user2, user3);
    }


    @Test
    @DisplayName("Test findAllFriendsByUserEntity by requesting a list of all friends of user2")
    void testFindAllFriendsByUserEntity_whenFriendsRequestedFromUser2_shouldReturnPopulatedSetOfFriends() {
        // Arrange
        Set<UserEntity> userEntities;
        int expectedFriends = 1;

        // Act
        userEntities = friendshipRepository.findAllFriendsByUserEntity(user2);

        // Assert
        assertThat(userEntities).hasSize(expectedFriends);
        assertThat(userEntities).containsOnly(user1);
    }


    @Test
    @DisplayName("test findFriendsIdByUserEntity correctly returns Set of user friends IDs")
    void testFindFriendsIdByUserEntity_whenFriendsIdRequested_shouldReturnPopulatedSetOfFriends() {
        // Arrange
        Set<UUID> uuidSet;
        byte expectedSize = 2;

        // Act
        uuidSet = friendshipRepository.findFriendsIdByUserEntity(user1);

        // Assert
        assertThat(uuidSet).hasSize(expectedSize);
        assertThat(uuidSet).containsOnly(user2.getId(), user3.getId());
    }
}
