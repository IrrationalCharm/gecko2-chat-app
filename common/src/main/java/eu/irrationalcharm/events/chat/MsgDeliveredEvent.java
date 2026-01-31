package eu.irrationalcharm.events.chat;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record MsgDeliveredEvent(
        @NotNull
        String conversationId,
        @NotNull
        String receiverId, //UserId of who confirmed message was delivered
        @NotNull
        Instant deliveryTimestamp
) implements ChatEvent{
}
