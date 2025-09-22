package eu.irrationalcharm.userservice.repository;

import eu.irrationalcharm.userservice.entity.UserFriendshipPreferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserFriendshipPreferenceRepository extends JpaRepository<UserFriendshipPreferenceEntity, Long> {


    @Query("select u from UserFriendshipPreferenceEntity u where u.userId = :userId and u.friendId = :friendId")
    Optional<UserFriendshipPreferenceEntity> findByUserIdAndFriendId(UUID userId, UUID friendId);
}
