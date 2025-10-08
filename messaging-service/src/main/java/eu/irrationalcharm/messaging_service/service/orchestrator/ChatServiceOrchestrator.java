package eu.irrationalcharm.messaging_service.service.orchestrator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.irrationalcharm.messaging_service.client.dto.UserSocialGraphDto;
import eu.irrationalcharm.messaging_service.config.websocket.WebSocketSessionRegistry;
import eu.irrationalcharm.messaging_service.dto.ChatMessageDto;
import eu.irrationalcharm.messaging_service.service.InternalUserService;
import eu.irrationalcharm.messaging_service.service.UserPresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatServiceOrchestrator {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final InternalUserService internalUserService;
    private final WebSocketSessionRegistry sessionRegistry;
    private final UserPresenceService userPresenceService;
    private final SimpMessagingTemplate simpMessagingTemplate;


    public void sendMessage(ChatMessageDto message, Authentication authentication) throws JsonProcessingException {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var senderSocialGraph = internalUserService.getUserSocialGraphByProviderId(authentication.getName());
        validateMessageOrThrow(message, authentication, senderSocialGraph);

        Optional<String> sessionIdOptional = sessionRegistry.getSession(message.recipientId());
        if ( sessionIdOptional.isEmpty() ) { //Send to redis to fanout

            if(userPresenceService.isUserOnline(message.recipientId())) {
                String payload = objectMapper.writeValueAsString(message);
                redisTemplate.convertAndSend("queue/private/messages", payload);
            } else
                System.out.println("Recipient is offline"); //TODO kafka implementation
        }

        if( sessionIdOptional.isPresent()) {
            internalSendPrivateMessage(message);
            System.out.println("message sent!");
        }
    }


    public void internalSendPrivateMessage(ChatMessageDto messageDto) {
        simpMessagingTemplate.convertAndSendToUser(messageDto.recipientId(), "/private", messageDto);
    }


    /**
     * Validates if the user is allowed to send message to recipient
     */
    private static void validateMessageOrThrow(ChatMessageDto message, Authentication senderAuth, UserSocialGraphDto senderSocialGraph) {
        //senderUsername same as authenticated?
        if (!message.userId().equals(senderAuth.getName())) {
            throw new RuntimeException("senderUsername must be the same as the authenticated user");
        }
        //Sending message to himself?
        if (message.userId().equals(message.recipientId())) {
            throw new RuntimeException("Cant send message to yourself");
        }
        //Are friends?
        Set<String> senderFriends = senderSocialGraph.friendsInternalId();
        boolean areFriends = senderFriends.stream()
                .anyMatch(userId -> userId.equals(message.recipientId()));
        if(!areFriends) {
            throw new RuntimeException("recipient isn't friends with " + message.userId());
        }
    }
}
