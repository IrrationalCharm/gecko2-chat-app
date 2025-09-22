package eu.irrationalcharm.userservice.repository;

import eu.irrationalcharm.userservice.dto.response.PublicUserResponseDto;
import eu.irrationalcharm.userservice.entity.FriendRequestEntity;
import eu.irrationalcharm.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendRequestRepository extends JpaRepository<FriendRequestEntity, Long> {

    @Query("""
        select u from FriendRequestEntity u where
            (u.initiator.id = :userA and u.receiver.id = :userB) or
            (u.initiator.id = :userB and u.receiver.id = :userA)
        """)
    Optional<FriendRequestEntity> findExistingRequestsBetweenUsers(UUID userA, UUID userB);


    @Query("""
    select new eu.irrationalcharm.userservice.dto.response.PublicUserResponseDto(
        u.initiator.username,
        u.initiator.displayName,
        u.initiator.profileBio,
        u.initiator.profileImageUrl)
        from FriendRequestEntity u where u.receiver.id = :receiver
    """)
    List<PublicUserResponseDto> findInitiatorAsDtoByReceiver(UUID receiver);

    @Query("select u from FriendRequestEntity u where u.initiator = :initiator and u.receiver = :receiver")
    Optional<FriendRequestEntity> findByInitiatorAndReceiver(UserEntity initiator, UserEntity receiver);
}
