package eu.irrationalcharm.userservice.enums;

public enum ErrorCode {

    //Hibernate validation
    VALIDATION_ERROR,

    //Friend Request domain
    FRIEND_REQUEST_SELF,
    FRIEND_REQUEST_BLOCKED_BY_USER,
    FRIEND_REQUEST_ALREADY_FRIENDS,
    FRIEND_REQUEST_EXISTS,
    FRIEND_REQUEST_NOT_FOUND,

    //User domain
    USERNAME_ALREADY_EXISTS,
    EMAIL_TAKEN,
    USERNAME_NOT_FOUND,
    ON_BOARDING_REQUIRED,
    RESOURCE_NOT_FOUND,

    //User Identity Provider domain
    IDP_ACCOUNT_ALREADY_LINKED,
    IDP_PROVIDER_ALREADY_REGISTERED,
    IDP_NOT_SUPPORTED

}
