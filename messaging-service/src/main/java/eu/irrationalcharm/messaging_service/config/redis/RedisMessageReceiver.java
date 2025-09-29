package eu.irrationalcharm.messaging_service.config.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.irrationalcharm.messaging_service.model.ChatMessageDto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;


@Service
@RequiredArgsConstructor
public class RedisMessageReceiver implements MessageListener {

    private final ObjectMapper objectMapper;


    /**
     * This is the listener of redis, if this client is
     * @param message message must not be {@literal null}.
     * @param pattern pattern matching the channel (if specified) - can be {@literal null}.
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            var json = new String(message.getBody());
            ChatMessageDto messageDto = objectMapper.readValue(json, ChatMessageDto.class);
            System.out.println("IT WORKS: " + messageDto);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }



        //simpMessagingTemplate.convertAndSendToUser(messageDto.recipientUsername(), "queue/private/messages", messageDto);

    }
}
