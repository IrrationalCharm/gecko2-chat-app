package eu.irrationalcharm.events.media_service;


//Producer: media-service
//Consumer: user-service
public record ProfilePictureUpdatedEvent(
        String userId,
        String thumbnailUrl,
        String fullUrl
) {
}
