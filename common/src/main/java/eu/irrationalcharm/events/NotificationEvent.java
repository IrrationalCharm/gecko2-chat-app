package eu.irrationalcharm.events;

import eu.irrationalcharm.enums.NotificationType;

public record NotificationEvent(
        NotificationType type,
        String requestReceiverId,
        Object payload
) {
}
