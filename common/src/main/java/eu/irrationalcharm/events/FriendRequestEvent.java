package eu.irrationalcharm.events;


//This is to be received
public record FriendRequestEvent(
        String requestId,
        String initiatorId,
        String initiatorUsername,
        String initiatorDisplayName,
        String initiatorProfileImageUrl,
        long createdAt
) {
}
