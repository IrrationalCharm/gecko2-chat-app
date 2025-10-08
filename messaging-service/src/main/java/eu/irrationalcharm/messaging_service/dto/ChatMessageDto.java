package eu.irrationalcharm.messaging_service.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatMessageDto(
        @NotBlank(message = "senderUsername cannot be empty")
        @Size(message = "username must be between 3 and 50 characters", min = 3, max = 20)
        String userId,

        @NotBlank(message = "recipientUsername cannot be empty")
        @Size(message = "username must be between 3 and 50 characters", min = 3, max = 20)
        String recipientId,

        @Size(max = 3000)
        String content,
        String timestamp
) {

}
