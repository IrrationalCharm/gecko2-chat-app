package eu.irrationalcharm.messaging_service.service.orchestrator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.irrationalcharm.messaging_service.config.websocket.WebSocketSessionRegistry;
import eu.irrationalcharm.messaging_service.dto.ChatMessageDto;
import eu.irrationalcharm.messaging_service.service.UserPresenceService;
import eu.irrationalcharm.messaging_service.service.event.MessageEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServiceOrchestrator {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final WebSocketSessionRegistry sessionRegistry;
    private final UserPresenceService userPresenceService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageEventProducer messageEventProducer;

    public void sendMessage(ChatMessageDto message) throws JsonProcessingException {
        messageEventProducer.produceMessageEvent(message);

        Optional<String> sessionIdOptional = sessionRegistry.getSession(message.recipientId());

        if (sessionIdOptional.isPresent()) {
            internalSendPrivateMessage(message);
            System.out.println("message sent!");
        }


        if ( sessionIdOptional.isEmpty() ) {
            //Send to redis to fanout
            if(userPresenceService.isUserOnline(message.recipientId())) {
                String payload = objectMapper.writeValueAsString(message);
                redisTemplate.convertAndSend("queue/private/messages", payload);
            } else
                System.out.println("Recipient is offline");
        }


    }


    public void internalSendPrivateMessage(ChatMessageDto messageDto) {
        simpMessagingTemplate.convertAndSendToUser(messageDto.recipientId(), "/private", messageDto);
    }




}
