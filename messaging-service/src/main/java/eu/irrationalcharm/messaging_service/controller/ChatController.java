package eu.irrationalcharm.messaging_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.irrationalcharm.messaging_service.dto.ChatMessageDto;
import eu.irrationalcharm.messaging_service.service.orchestrator.ChatServiceOrchestrator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {


    private final ChatServiceOrchestrator chatServiceOrchestrator;


    @MessageMapping("/chat")
    public void sendMessage(@Payload @Valid ChatMessageDto message,
                            Authentication authentication) throws JsonProcessingException {

        chatServiceOrchestrator.sendMessage(message, authentication);

    }

}
