package eu.irrationalcharm.messaging_service.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatMessageDto(
        @NotBlank(message = "userId cannot be empty")
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
