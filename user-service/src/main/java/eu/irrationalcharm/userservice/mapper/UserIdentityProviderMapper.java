package eu.irrationalcharm.userservice.mapper;

import eu.irrationalcharm.userservice.dto.UserIdentityProviderDto;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.userservice.entity.UserIdentityProviderEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public final class UserIdentityProviderMapper {

    public static UserIdentityProviderEntity mapToEntity(@NotNull @Valid UserIdentityProviderDto idpDto,
                                                         @NotNull UserEntity userEntity) {
        var idpEntity = new UserIdentityProviderEntity();
        idpEntity.setUserEntity(userEntity);
        idpEntity.setProvider(idpDto.provider());
        idpEntity.setProviderUserId(idpDto.providerUserId());
        idpEntity.setEmail(idpDto.email());


        return idpEntity;
    }

    public static UserIdentityProviderDto mapToDto(@NotNull UserIdentityProviderEntity idpEntity) {
        return UserIdentityProviderDto.builder()
                .userId(idpEntity.getUserEntity().getId())
                .provider(idpEntity.getProvider())
                .providerUserId(idpEntity.getProviderUserId())
                .email(idpEntity.getEmail())
                .build();
    }
}
