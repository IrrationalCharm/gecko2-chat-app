package eu.irrationalcharm.userservice.dto.response;

public record PatchFriendPreferenceDto (
        Boolean isBlocked,
        Boolean isMuted,
        Boolean isPinned
){
}
