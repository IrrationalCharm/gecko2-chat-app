package eu.irrationalcharm.messaging_service.service.orchestrator;

import eu.irrationalcharm.events.chat.MessageEvent;
import eu.irrationalcharm.messaging_service.config.websocket.WebSocketSessionRegistry;
import eu.irrationalcharm.messaging_service.dto.request.SendMessageRequest;
import eu.irrationalcharm.messaging_service.dto.response.*;
import eu.irrationalcharm.messaging_service.enums.MessageType;
import eu.irrationalcharm.messaging_service.mapper.ChatEventMapper;
import eu.irrationalcharm.messaging_service.mapper.MessageMapper;
import eu.irrationalcharm.messaging_service.service.UserPresenceService;
import eu.irrationalcharm.messaging_service.service.event.MessageEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceOrchestrator {

    private final StringRedisTemplate redisTemplate;
    private final JsonMapper objectMapper;
    private final WebSocketSessionRegistry sessionRegistry;
    private final UserPresenceService userPresenceService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageEventProducer messageEventProducer;

    public void sendMessage(SendMessageRequest message) {
        Instant now = Instant.now();
        MessageEvent event = ChatEventMapper.toMessageEvent(message, now);
        //Push to Kafka
        messageEventProducer.produceMessageEvent(event);

        Optional<String> sessionIdOptional = sessionRegistry.getSession(message.recipientId());

        if (sessionIdOptional.isPresent()) {
            ChatMessagePayload messagePayload = MessageMapper.mapToChatMessagePayload(message, now); //Exactly the same, just to keep with naming
            internalSendPrivateMessage(message.recipientId(), messagePayload);
            log.info("Message has been sent");
        }

        var ackMessage = new MessageSentPayload(MessageType.MESSAGE_SENT_SERVER, message.clientMsgId(), now.toString());
        internalSendPrivateMessage(message.senderId(), ackMessage);

        if ( sessionIdOptional.isEmpty() ) {
            //Send to redis to fanout
            if(userPresenceService.isUserOnline(message.recipientId())) {
                String payload = objectMapper.writeValueAsString(message);
                redisTemplate.convertAndSend("queue/private/messages", payload);
            } else
                System.out.println("Recipient is offline");
        }

    }


    public void internalSendPrivateMessage(String recipientId, ServerMessage message) {
        switch(message) {
            case ChatMessagePayload messageDto -> simpMessagingTemplate.convertAndSendToUser(recipientId, "/private", messageDto);
            case MessageSentPayload ackMessage -> simpMessagingTemplate.convertAndSendToUser(recipientId, "/private", ackMessage);
            case FriendRequestPayload _ -> {
            }
            case MessageDeliveredPayload _ -> {
            }
            case MessageReadPayload _ -> {
            }
            default -> throw new IllegalStateException("Unexpected value: " + message);
        }
    }

}
