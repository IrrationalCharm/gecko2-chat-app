package eu.irrationalcharm.messaging_service.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtClaims {

    public static final String ISSUER = "iss";
    public static final String SUBJECT = "sub";
    public static final String EXPIRATION = "exp";
    public static final String EMAIL = "email";

    //Added attributes after onBoarding
    public static final String USERNAME_APP = "username_app";
    public static final String INTERNAL_ID = "internal_id";
}
