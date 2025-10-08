package eu.irrationalcharm.userservice.repository;


import eu.irrationalcharm.userservice.entity.FriendshipEntity;
import eu.irrationalcharm.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * On lookup, friendA is always the higher UUID value of the pair.
 */
public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {

    boolean existsByFriendAAndFriendB(UserEntity friendA, UserEntity friendB);

    Optional<FriendshipEntity> findByFriendAAndFriendB(UserEntity friendA, UserEntity friendB);

    @Query("""
        select f.friendB from FriendshipEntity f where f.friendA = :userEntity
        UNION
        select f.friendA from FriendshipEntity f where f.friendB = :userEntity
    """)
    Set<UserEntity> findAllFriendsByUserEntity(UserEntity userEntity);

    @Query("""
        select f.friendB.id from FriendshipEntity f where f.friendA = :userEntity
        UNION ALL
        select f.friendA.id from FriendshipEntity f where f.friendB = :userEntity
    """)
    Set<UUID> findFriendsIdByUserEntity(UserEntity userEntity);

}
