package eu.irrationalcharm.messaging_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.irrationalcharm.messaging_service.model.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {


    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;


    @MessageMapping("/chat")
    public void sendMessage(@Payload ChatMessageDto message, Authentication authentication) throws JsonProcessingException {


        String payload = objectMapper.writeValueAsString(message);
        redisTemplate.convertAndSend("queue/private/messages", payload);

    }

    @PostMapping("/send")
    public ResponseEntity<Object> sendMessageRest(@RequestBody ChatMessageDto messageDto) throws JsonProcessingException {
        System.out.println("message sent");
        String json = objectMapper.writeValueAsString(messageDto);

        redisTemplate.convertAndSend("queue/private/messages", json);

        return ResponseEntity.ok(null);
    }
}
