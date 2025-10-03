package eu.irrationalcharm.userservice.event;

import lombok.Builder;

@Builder
public record UserUpdateEvent(String username, String providerId) {
}
