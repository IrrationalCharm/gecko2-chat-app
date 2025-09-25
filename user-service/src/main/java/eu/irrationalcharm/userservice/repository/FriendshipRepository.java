package eu.irrationalcharm.userservice.repository;


import eu.irrationalcharm.userservice.entity.FriendshipEntity;
import eu.irrationalcharm.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

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
    Set<UserEntity> findAllFriendsByUserId(UserEntity userEntity);

}
