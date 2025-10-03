package eu.irrationalcharm.userservice.repository;

import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.userservice.entity.UserIdentityProviderEntity;
import eu.irrationalcharm.userservice.enums.IdentityProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserIdentityProviderRepository extends JpaRepository<UserIdentityProviderEntity, Long> {

    Optional<UserIdentityProviderEntity> findByProviderUserId(String providerUserId);

    @Query("select u.providerUserId from UserIdentityProviderEntity u where u.userEntity = :userEntity")
    List<String> findProviderIdByUserEntity(UserEntity userEntity);

    @Query("select u from UserIdentityProviderEntity u where u.userEntity.id = :userId and u.provider = :provider")
    Optional<UserIdentityProviderEntity> findByUserIdAndProvider(UUID userId, IdentityProviderType provider);

    @Query("select u.userEntity from UserIdentityProviderEntity u where u.provider = :provider and u.providerUserId = :providerUserId ")
    Optional<UserEntity> findUserIdByProviderAndProviderUserId(IdentityProviderType provider, String providerUserId);


}
