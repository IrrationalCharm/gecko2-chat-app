package eu.irrationalcharm.messaging_service.service.orchestrator;

import eu.irrationalcharm.messaging_service.dto.request.DeliveredReceiptRequest;
import eu.irrationalcharm.messaging_service.dto.request.ReadReceiptRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Manages incoming message status from client: message delivered, message read
 */
@Service
@RequiredArgsConstructor
public class MessageStatusServiceOrchestrator {

    private final SimpMessagingTemplate simpMessagingTemplate;


    public void deliveredReceipt(DeliveredReceiptRequest message) {


    }

    public void messageReadReceipt(ReadReceiptRequest message) {
    }
}
