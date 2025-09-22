package eu.irrationalcharm.userservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtClaims {

    public static final String ISSUER = "iss";
    public static final String SUBJECT = "sub";
    public static final String EXPIRATION = "exp";
    public static final String EMAIL = "email";
}
