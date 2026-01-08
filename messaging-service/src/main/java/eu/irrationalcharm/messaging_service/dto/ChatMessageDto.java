package eu.irrationalcharm.messaging_service.dto;


import eu.irrationalcharm.messaging_service.validation.MessageValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@MessageValid
public record ChatMessageDto(
        @NotBlank(message = "internalId cannot be empty")
        @Size(message = "Please provide a valid Internal User Id", max = 36)
        String userId,

        @NotBlank(message = "recipientId cannot be empty")
        @Size(message = "Please provide a valid Internal User I", max = 36)
        String recipientId,

        @Size(max = 3000)
        String content,
        String timestamp
) {

}
