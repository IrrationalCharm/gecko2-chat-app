package eu.irrationalcharm.userservice.enums;

import eu.irrationalcharm.userservice.exception.BusinessException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Getter
public enum IdentityProviderType {
    KEYCLOAK("http://localhost:8080/realms/gecko2-realm"),
    GOOGLE("google");

    private final String issuer;

    IdentityProviderType(String issuer) {
        this.issuer = issuer;
    }

    /**
     * Returns the type of IDP by providing the issuer (iss)
     * @param issuer url of the IDP
     * @return type of IdentityProviderType
     * @throws BusinessException if url is not recognized or supported
     */
    public static IdentityProviderType fromIssuer(String issuer) {
        return Arrays.stream(values())
                .filter(provider -> provider.getIssuer().equals(issuer))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.BAD_REQUEST,
                        ErrorCode.IDP_NOT_SUPPORTED,
                        String.format("Identity provider %s not supported", issuer)));
    }
}

