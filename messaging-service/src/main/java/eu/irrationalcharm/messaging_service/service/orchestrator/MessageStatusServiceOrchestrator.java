package eu.irrationalcharm.messaging_service.service.orchestrator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.irrationalcharm.events.chat.MsgDeliveredEvent;
import eu.irrationalcharm.messaging_service.config.websocket.WebSocketSessionRegistry;
import eu.irrationalcharm.messaging_service.dto.request.DeliveredReceiptRequest;
import eu.irrationalcharm.messaging_service.dto.request.ReadReceiptRequest;
import eu.irrationalcharm.messaging_service.dto.response.ChatMessagePayload;
import eu.irrationalcharm.messaging_service.dto.response.MessageDeliveredPayload;
import eu.irrationalcharm.messaging_service.dto.response.ServerMessage;
import eu.irrationalcharm.messaging_service.mapper.ChatEventMapper;
import eu.irrationalcharm.messaging_service.mapper.MessageMapper;
import eu.irrationalcharm.messaging_service.service.UserPresenceService;
import eu.irrationalcharm.messaging_service.service.event.MessageEventProducer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * Manages incoming message status from client: message delivered, message read
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageStatusServiceOrchestrator {

    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageEventProducer messageEventProducer;
    private final UserPresenceService userPresenceService;
    private final ObjectMapper objectMapper;
    private final WebSocketSessionRegistry sessionRegistry;

    public void deliveredReceipt(DeliveredReceiptRequest message) throws JsonProcessingException {
        Instant now = Instant.now();
        MsgDeliveredEvent event = ChatEventMapper.toMsgDeliveredEvent(message, now);

        messageEventProducer.produceMessageEvent(event); //Push to Kafka

        Optional<String> sessionIdOptional = sessionRegistry.getSession(message.recipientId());

        if (sessionIdOptional.isPresent()) {
            MessageDeliveredPayload messagePayload = MessageMapper.mapToMessageDeliveredPayload(message, now);
            deliverMessageToWebsocket(message.recipientId(), messagePayload);
            log.info("Message has been sent to recipient");
        }


        if ( sessionIdOptional.isEmpty() ) {
            //Send to redis to fanout
            if (userPresenceService.isUserOnline(message.recipientId())) { //Checks if user is connected at all on redis
                String payload = objectMapper.writeValueAsString(message);
                redisTemplate.convertAndSend("queue/private/messages", payload);
            } else
                System.out.println("Recipient is offline");
        }

    }

    private void deliverMessageToWebsocket(@NotNull String recipientId, ServerMessage payload) {
        simpMessagingTemplate.convertAndSendToUser(recipientId, "/private", payload);
    }

    public void messageReadReceipt(ReadReceiptRequest message) {


    }
}
