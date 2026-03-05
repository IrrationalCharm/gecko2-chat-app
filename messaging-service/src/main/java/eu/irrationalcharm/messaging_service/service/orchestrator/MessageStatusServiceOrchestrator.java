package eu.irrationalcharm.messaging_service.service.orchestrator;


import eu.irrationalcharm.events.chat.MsgDeliveredEvent;
import eu.irrationalcharm.events.chat.MsgReadEvent;
import eu.irrationalcharm.messaging_service.config.websocket.WebSocketSessionRegistry;
import eu.irrationalcharm.messaging_service.dto.request.DeliveredReceiptRequest;
import eu.irrationalcharm.messaging_service.dto.request.ReadReceiptRequest;
import eu.irrationalcharm.messaging_service.dto.response.MessageDeliveredPayload;
import eu.irrationalcharm.messaging_service.dto.response.MessageReadPayload;
import eu.irrationalcharm.messaging_service.dto.response.ServerMessage;
import eu.irrationalcharm.messaging_service.mapper.ChatEventMapper;
import eu.irrationalcharm.messaging_service.mapper.MessageMapper;
import eu.irrationalcharm.messaging_service.service.UserPresenceService;
import eu.irrationalcharm.messaging_service.service.event.MessageEventProducer;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

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
    private final JsonMapper objectMapper;
    private final WebSocketSessionRegistry sessionRegistry;

    public void deliveredReceipt(DeliveredReceiptRequest request) {
        Instant now = Instant.now();
        MsgDeliveredEvent event = ChatEventMapper.toMsgDeliveredEvent(request, now);

        messageEventProducer.produceMessageEvent(event); //Push to Kafka

        Optional<String> sessionIdOptional = sessionRegistry.getSession(request.recipientId());

        if (sessionIdOptional.isPresent()) {
            MessageDeliveredPayload messagePayload = MessageMapper.mapToMessageDeliveredPayload(request, now);
            deliverMessageToWebsocket(request.recipientId(), messagePayload);

            log.debug("Delivered receipt for message {} successfully routed locally to user {}", request.messageId(), request.recipientId());
        }


        if ( sessionIdOptional.isEmpty() ) {
            //Send to redis to fanout
            if (userPresenceService.isUserOnline(request.recipientId())) { //Checks if user is connected at all on redis
                log.debug("Recipient {} is not on this instance but is online. Fanning out delivered receipt to Redis Pub/Sub.", request.recipientId());
                try {
                    String payload = objectMapper.writeValueAsString(request);
                    redisTemplate.convertAndSend("queue/private/messages", payload);
                } catch (Exception e) {
                    log.error("Failed to serialize DeliveredReceiptRequest for Redis fanout. Request: {}", request, e);
                }
            } else
                log.debug("Recipient {} is entirely offline. Delivered receipt processed but not sent via websocket.", request.recipientId());
        }

    }

    private void deliverMessageToWebsocket(@NotNull String recipientId, ServerMessage payload) {
        simpMessagingTemplate.convertAndSendToUser(recipientId, "/private", payload);
    }

    public void messageReadReceipt(ReadReceiptRequest request) {
        Instant readTimestamp = Instant.now();
        MsgReadEvent event = ChatEventMapper.toMsgReadEvent(request, readTimestamp);

        messageEventProducer.produceMessageEvent(event); //Push to Kafka

        Optional<String> sessionIdOptional = sessionRegistry.getSession(request.recipientId());

        if (sessionIdOptional.isPresent()) {
            MessageReadPayload messagePayload = MessageMapper.mapToMessageReadPayload(request, readTimestamp);
            deliverMessageToWebsocket(request.recipientId(), messagePayload);
            log.debug("Read receipt for conversation {} successfully routed locally to user {}", request.conversationId(), request.recipientId());
        }


        if ( sessionIdOptional.isEmpty() ) {
            //Send to redis to fanout
            if (userPresenceService.isUserOnline(request.recipientId())) { //Checks if user is connected at all on redis
                log.debug("Recipient {} is not on this instance but is online. Fanning out read receipt to Redis Pub/Sub.", request.recipientId());
                try {
                    String payload = objectMapper.writeValueAsString(request);
                    redisTemplate.convertAndSend("queue/private/messages", payload);
                } catch (Exception e) {
                    log.error("Failed to serialize ReadReceiptRequest for Redis fanout. Request: {}", request, e);
                }
            } else
                log.debug("Recipient {} is entirely offline. Read receipt processed but not sent via websocket.", request.recipientId());
        }
    }
}
