package eu.irrationalcharm.events;

import eu.irrationalcharm.enums.TextType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record MessageEvent(
        @NotNull
        String conversationId,

        @NotNull
        String senderId,

        @NotNull
        String recipientId,

        @NotEmpty
        @Size(min = 1, max = 3000)
        String content,

        @NotNull
        LocalDateTime timestamp,

        @NotNull
        TextType textType
) {
}
