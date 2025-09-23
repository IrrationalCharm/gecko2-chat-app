package eu.irrationalcharm.userservice.enums;

public enum SuccessfulCode {

    //User domain
    USER_CREATED,
    USER_FOUND,
    USER_UPDATED,
    USER_PROFILE_COMPLETE,
    ONBOARDING_REQUIRED,

    //Friend requests domain
    FRIENDS_LIST,
    FRIEND_REQUEST_SENT,
    FRIEND_REQUEST_DECLINED,
    FRIEND_REQUEST_ACCEPTED,
    FRIEND_REQUEST_CANCELLED,
    FRIEND_REQUEST_PENDING,

    FRIEND_PREFERENCE_FOUND, //Friendship domain
    FRIEND_PREFERENCE_UPDATED,

}
