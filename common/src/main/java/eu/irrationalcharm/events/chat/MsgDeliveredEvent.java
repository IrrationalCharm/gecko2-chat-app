package eu.irrationalcharm.events.chat;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

//To update lastDeliveredTimestamp in Conversation table
public record MsgDeliveredEvent(
        @NotNull
        String conversationId,
        @NotNull
        String recipientId, //User that confirmed message was delivered
        @NotNull
        Instant timestamp
) implements ChatEvent{
}
