package eu.irrationalcharm.messaging_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.irrationalcharm.messaging_service.dto.request.DeliveredReceiptRequest;
import eu.irrationalcharm.messaging_service.dto.request.ReadReceiptRequest;
import eu.irrationalcharm.messaging_service.dto.request.SendMessageRequest;
import eu.irrationalcharm.messaging_service.dto.request.TypingStatusRequest;
import eu.irrationalcharm.messaging_service.service.orchestrator.ChatServiceOrchestrator;
import eu.irrationalcharm.messaging_service.service.orchestrator.MessageStatusServiceOrchestrator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatServiceOrchestrator chatServiceOrchestrator;
    private final MessageStatusServiceOrchestrator messageStatusService;


    @MessageMapping("/chat")
    public void sendMessage(@Payload @Valid SendMessageRequest message) throws JsonProcessingException {
        chatServiceOrchestrator.sendMessage(message);
    }

    @MessageMapping("/delivered-receipt")
    public void deliveryReceipt(@Payload @Valid DeliveredReceiptRequest message) throws JsonProcessingException {
        messageStatusService.deliveredReceipt(message);
    }

    @MessageMapping("/read-receipt")
    public void readReceipt(@Payload @Valid ReadReceiptRequest message) throws JsonProcessingException {
        messageStatusService.messageReadReceipt(message);
    }

    @MessageMapping("/typing")
    public void sendMessage(@Payload @Valid TypingStatusRequest message) throws JsonProcessingException {
        //chatServiceOrchestrator.sendMessage(message);
    }
}
