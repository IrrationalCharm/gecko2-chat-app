package eu.irrationalcharm.userservice.repository;

import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.userservice.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, UUID> {

    boolean existsUserEntityByUsername(String username);

    boolean existsUserEntityByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

    @Query("""
    select new eu.irrationalcharm.dto.user_service.PublicUserResponseDto(
        u.id,
        u.username,
        u.displayName,
        u.profileBio,
        u.profileImageUrl)
        from UserEntity u where u.id in :userFriends""")
    List<PublicUserResponseDto> findAllUsersByUserId(List<UUID> userFriends);

    boolean existsUserEntityByProviderId(String providerId);

}
