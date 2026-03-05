package eu.irrationalcharm.events.chat;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record MsgReadEvent(
        @NotNull
        String conversationId,
        @NotNull
        String readerId, //UserId of user who just read the message they received.
        @NotNull
        Instant readTimestamp
) implements ChatEvent {

}
