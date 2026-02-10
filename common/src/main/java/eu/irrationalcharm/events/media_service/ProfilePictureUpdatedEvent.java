package eu.irrationalcharm.events.media_service;


import jakarta.validation.constraints.NotNull;

//Producer: media-service
//Consumer: user-service
public record ProfilePictureUpdatedEvent(
        @NotNull
        String userId,
        @NotNull
        String thumbnailUrl,
        @NotNull
        String fullUrl
) {
}
