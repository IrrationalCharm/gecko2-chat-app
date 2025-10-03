package eu.irrationalcharm.userservice.service;

import eu.irrationalcharm.userservice.constants.JwtClaims;
import eu.irrationalcharm.userservice.dto.UserIdentityProviderDto;
import eu.irrationalcharm.userservice.entity.UserEntity;
import eu.irrationalcharm.userservice.enums.ErrorCode;
import eu.irrationalcharm.userservice.enums.IdentityProviderType;
import eu.irrationalcharm.userservice.exception.BusinessException;
import eu.irrationalcharm.userservice.mapper.UserIdentityProviderMapper;
import eu.irrationalcharm.userservice.repository.UserIdentityProviderRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class IdentityProviderService {

    private final UserIdentityProviderRepository userIdentityProviderRepository;

    @Transactional
    public void persistIdentityProvider(Jwt authJwt, UserEntity userEntity) {
        validateNewProviderAssociationOrThrow(authJwt, userEntity);

        String issuer = authJwt.getClaim(JwtClaims.ISSUER).toString();
        var identityProviderType = IdentityProviderType.fromIssuer(issuer);
        String providerUserId = authJwt.getClaim(JwtClaims.SUBJECT);

        var newIdpDto = UserIdentityProviderDto.builder()
                .providerUserId(providerUserId)
                .userId(userEntity.getId())
                .provider(identityProviderType)
                .email(authJwt.getClaim(JwtClaims.EMAIL))
                .build();

        var identityProviderEntity = UserIdentityProviderMapper.mapToEntity(newIdpDto, userEntity);
        userIdentityProviderRepository.save(identityProviderEntity);
    }

    @Transactional(readOnly = true)
    public void validateNewProviderAssociationOrThrow(Jwt authJwt, UserEntity userEntity) {
        String issuer = authJwt.getClaim(JwtClaims.ISSUER).toString();
        var identityProviderType = IdentityProviderType.fromIssuer(issuer);
        String providerUserId = authJwt.getClaim(JwtClaims.SUBJECT);

        //Case 1: Provider user ID already exists globally
        userIdentityProviderRepository.findByProviderUserId(providerUserId)
                .ifPresent(userIdp -> {
                    throw new BusinessException(
                            HttpStatus.CONFLICT,
                            ErrorCode.IDP_PROVIDER_ALREADY_REGISTERED,
                            String.format("This account has already been associated with %s", identityProviderType.toString().toLowerCase()));
                });

        //Case 2: User already has an identity with this provider
        userIdentityProviderRepository.findByUserIdAndProvider(userEntity.getId(), identityProviderType)
                .ifPresent(userIdp -> {
                    throw new BusinessException(HttpStatus.CONFLICT,
                            ErrorCode.IDP_ACCOUNT_ALREADY_LINKED,
                            String.format("This user_id has already been registered in %s", identityProviderType.toString().toLowerCase()));
                });

    }


    @Transactional(readOnly = true)
    public List<String> findProviderIdByUserId(UserEntity userEntity) {
        return userIdentityProviderRepository.findProviderIdByUserEntity(userEntity);
    }

}
