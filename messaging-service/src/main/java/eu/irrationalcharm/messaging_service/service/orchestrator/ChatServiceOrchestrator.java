package eu.irrationalcharm.messaging_service.service.orchestrator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.irrationalcharm.messaging_service.client.dto.UserSocialGraphDto;
import eu.irrationalcharm.messaging_service.config.websocket.WebSocketSessionRegistry;
import eu.irrationalcharm.messaging_service.model.ChatMessageDto;
import eu.irrationalcharm.messaging_service.service.InternalUserService;
import eu.irrationalcharm.messaging_service.service.UserPresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
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

    public void sendMessage(ChatMessageDto message, Authentication authentication) throws JsonProcessingException {
        var userSocialGraph = internalUserService.getUserSocialGraphDto(authentication.getName());
        validateMessageOrThrow(message, userSocialGraph);

        Optional<String> sessionIdOptional = sessionRegistry.getSession(message.recipientUsername());
        if ( sessionIdOptional.isEmpty() ) { //Send to redis to fanout

            if(userPresenceService.isUserOnline(message.recipientUsername())) {
                String payload = objectMapper.writeValueAsString(message);
                redisTemplate.convertAndSend("queue/private/messages", payload);
            } else {
                System.out.println("Message received but user isn't online. To be implemented offline");
                //TODO send message to kafka
            }
        }

        if( sessionIdOptional.isPresent()) {
            String sessionId = sessionIdOptional.get();
        }

    }


    /**
     * Validates if the user is allowed to send message to recipient
     */
    private static void validateMessageOrThrow(ChatMessageDto message, UserSocialGraphDto userSocialGraph) {
        //senderUsername same as authenticated?
        if (!message.senderUsername().equals(userSocialGraph.username())) {
            throw new RuntimeException("senderUsername must be the same as the authenticated user");
        }
        //Sending message to himself?
        if (message.senderUsername().equals(message.recipientUsername())) {
            throw new RuntimeException("Cant send message to yourself");
        }
        //Are friends?
        Set<String> senderFriends = userSocialGraph.friendsUsername();
        boolean areFriends = senderFriends.stream()
                .anyMatch(username -> username.equals(message.recipientUsername()));
        if(!areFriends) {
            throw new RuntimeException("recipient isn't friends with " + message.senderUsername());
        }
    }
}
