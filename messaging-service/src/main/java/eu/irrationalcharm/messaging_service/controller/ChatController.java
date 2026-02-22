package eu.irrationalcharm.messaging_service.controller;

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
    public void sendMessage(@Payload @Valid SendMessageRequest message) {
        chatServiceOrchestrator.sendMessage(message);
    }

    @MessageMapping("/delivered-receipt")
    public void deliveryReceipt(@Payload @Valid DeliveredReceiptRequest message) {
        log.debug("delivered-receipt received{}", message);
        messageStatusService.deliveredReceipt(message);
    }

    @MessageMapping("/read-receipt")
    public void readReceipt(@Payload @Valid ReadReceiptRequest message) {
        log.debug("read-receipt received{}", message);
        messageStatusService.messageReadReceipt(message);
    }


    //NGL this implementation sucks, but for real I couldn't find a better way to assure established connection
    @MessageMapping("/ping")
    @SendToUser("/private")
    public String handlePing(String payload) {
        log.trace("Ping received, returning pong...");
        return "PONG";
    }

    @MessageMapping("/typing")
    public void sendMessage(@Payload @Valid TypingStatusRequest message) {
        //chatServiceOrchestrator.sendMessage(message);
    }
}
