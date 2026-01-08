package eu.irrationalcharm.userservice.repository;

import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.userservice.entity.FriendRequestEntity;
import eu.irrationalcharm.userservice.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;


@Testcontainers
@DataJpaTest
class FriendRequestRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    FriendRequestRepository friendRequestRepository;

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:latest");

    UserEntity user1;
    UserEntity user2;
    UserEntity user3;
    FriendRequestEntity friendRequestUser1_3;
    FriendRequestEntity friendRequestUser3_2;


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

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        friendRequestUser1_3 = new FriendRequestEntity();
        friendRequestUser1_3.setInitiator(user1);
        friendRequestUser1_3.setReceiver(user3);

        friendRequestUser3_2 = new FriendRequestEntity();
        friendRequestUser3_2.setInitiator(user3);
        friendRequestUser3_2.setReceiver(user2);

        entityManager.persistAndFlush(friendRequestUser1_3);
        entityManager.persistAndFlush(friendRequestUser3_2);
    }


    @Test
    @DisplayName("Test if PostgreSQL container is running")
    void isTestContainerRunning() {
        assertThat(postgres.isRunning()).isTrue();
    }


    @Test
    @DisplayName("Test findExistingRequestsBetweenUsers when querying for a non existent friend request")
    void testFindExistingRequestsBetweenUsers_whenANonexistentRequestIsQueried_shouldReturnEmptyOptional() {
        // Arrange
        Optional<FriendRequestEntity> result;

        // Act
        result = friendRequestRepository.findExistingRequestsBetweenUsers(user1.getId(), user2.getId());

        // Assert
        assertThat(result.isEmpty()).isTrue();
    }


    @Test
    @DisplayName("Test findExistingRequestsBetweenUsers returns the correct friend request entity")
    void testFindExistingRequestsBetweenUsers_whenAFriendRequestIsQueried_shouldReturnFriendRequest() {
        // Arrange
        Optional<FriendRequestEntity> friendRequestEntityOptional;

        // Act
        friendRequestEntityOptional = friendRequestRepository.findExistingRequestsBetweenUsers(user3.getId(), user1.getId());

        // Assert
        assertThat(friendRequestEntityOptional.isPresent()).isTrue();
        FriendRequestEntity friendRequest = friendRequestEntityOptional.get();

        assertThat(friendRequest.getId()).isEqualTo(friendRequestUser1_3.getId());
        assertThat(friendRequest.getInitiator()).isEqualTo(friendRequestUser1_3.getInitiator());
        assertThat(friendRequest.getReceiver()).isEqualTo(friendRequestUser1_3.getReceiver());
    }


    @Test
    @DisplayName("test findInitiatorAsDtoByReceiver by providing Receiver UUID and it should return the friend request initiator DTO")
    void testFindInitiatorAsDtoByReceiver_whenUUIDFromReceiverIsProvided_shouldReturnDtoFromPendingFriendRequests() {
        // Arrange
        UserEntity initiator = friendRequestUser3_2.getInitiator();
        UserEntity receiver = friendRequestUser3_2.getReceiver();
        int expectedSize = 1;

        // Act
        List<PublicUserResponseDto> friendRequestInitiatorList = friendRequestRepository.findPendingFriendRequestsAsDto(receiver.getId());

        // Assert
        assertThat(friendRequestInitiatorList)
                .hasSize(expectedSize)
                .extracting(
                        PublicUserResponseDto::internalId,
                        PublicUserResponseDto::username,
                        PublicUserResponseDto::displayName,
                        PublicUserResponseDto::profileBio,
                        PublicUserResponseDto::profileImageUrl)
                .containsOnly(
                        tuple(
                                initiator.getId(),
                                initiator.getUsername(),
                                initiator.getDisplayName(),
                                initiator.getProfileBio(),
                                initiator.getProfileImageUrl()
                        )
                );
    }


    @Test
    @DisplayName("test findInitiatorAsDtoByReceiver by providing a random UUID that doesnt have pending friend requests")
    void testFindPendingFriendRequestsAsDtoIdWhomDoesntHaveFriendRequests_shouldReturnDEmptyList() {
        // Act
        List<PublicUserResponseDto> friendRequestInitiatorList = friendRequestRepository.findPendingFriendRequestsAsDto(UUID.randomUUID());

        // Assert
        assertThat(friendRequestInitiatorList)
                .isEmpty();
    }


    @Test
    @DisplayName("test findByInitiatorAndReceiver by sending initiator and receiver entities, should return correct FriendRequestEntity")
    void testFindByInitiatorAndReceiver_whenCorrectInitiatorAndReceiverProvided_shouldReturnFriendRequestEntity() {
        // Act
        Optional<FriendRequestEntity> friendRequestEntityOptional = friendRequestRepository.findByInitiatorAndReceiver(friendRequestUser3_2.getInitiator(), friendRequestUser3_2.getReceiver());


        // Assert
        assertThat(friendRequestEntityOptional.isPresent()).isTrue();
        FriendRequestEntity friendRequestEntity = friendRequestEntityOptional.get();

        assertThat(friendRequestEntity.getReceiver())
                .isEqualTo(friendRequestUser3_2.getReceiver());

        assertThat(friendRequestEntity.getInitiator())
                .isEqualTo(friendRequestUser3_2.getInitiator());
    }
}
