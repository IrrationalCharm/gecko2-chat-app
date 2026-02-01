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
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Slf4j
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
        log.debug("delivered-receipt received{}", message);
        messageStatusService.deliveredReceipt(message);
    }

    @MessageMapping("/read-receipt")
    public void readReceipt(@Payload @Valid ReadReceiptRequest message) throws JsonProcessingException {
        log.debug("read-receipt received{}", message);
        messageStatusService.messageReadReceipt(message);
    }

    @MessageMapping("/ping")
    @SendToUser("/private")
    public String handlePing(String payload) {
        log.info("Ping received, returning pong...");
        return "PONG";
    }

    @MessageMapping("/typing")
    public void sendMessage(@Payload @Valid TypingStatusRequest message) throws JsonProcessingException {
        //chatServiceOrchestrator.sendMessage(message);
    }
}
