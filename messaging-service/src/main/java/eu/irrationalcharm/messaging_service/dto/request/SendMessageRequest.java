package eu.irrationalcharm.messaging_service.dto.request;

import eu.irrationalcharm.enums.TextType;
import eu.irrationalcharm.messaging_service.enums.MessageType;
import eu.irrationalcharm.messaging_service.validation.MessageValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.With;

@MessageValid
public record SendMessageRequest(
        @NotNull
        MessageType type,

        @NotNull
        @Pattern(regexp = "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$") //UUID regex
        String clientMsgId, //Temporary

        @NotBlank(message = "internalId cannot be empty")
        @Size(message = "Please provide a valid Internal User Id", max = 36)
        String senderId,

        @NotBlank(message = "receiverId cannot be empty")
        @Size(message = "Please provide a valid Internal User I", max = 36)
        String recipientId,

        @NotNull
        TextType textType,

        @NotBlank(message = "content of message cannot be empty")
        @Size(max = 3000)
        String content,

        @With String timestamp

) implements ClientMessage {
}
