package eu.irrationalcharm.events.chat;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record MsgReadEvent(
        @NotNull
        String conversationId,
        @NotNull
        String recipientId, //User that confirmed message was read
        @NotNull
        Instant timestamp
) implements ChatEvent {

}
