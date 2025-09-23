package eu.irrationalcharm.userservice.dto.response;

public record FriendPreferenceDto(
        String friendUsername,
        boolean isBlocked,
        boolean isMuted,
        boolean isPinned

) {
}
