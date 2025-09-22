package eu.irrationalcharm.userservice.repository;


import eu.irrationalcharm.userservice.entity.FriendshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {

    //Warning: friendA needs to be the higher UUID value of the friendship
    Optional<FriendshipEntity> findByFriendAAndFriendB(UUID friendA, UUID friendB);

    @Query("""
        select f.friendB from FriendshipEntity f where f.friendA = :userId
        UNION
        select f.friendA from FriendshipEntity f where f.friendB = :userId
    """)
    List<UUID> findAllFriendsByUserId(UUID userId);
}
