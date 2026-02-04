package eu.irrationalcharm.messaging_service.config.redis;

import eu.irrationalcharm.messaging_service.config.websocket.WebSocketSessionRegistry;
import eu.irrationalcharm.messaging_service.dto.response.ChatMessagePayload;
import eu.irrationalcharm.messaging_service.service.orchestrator.ChatServiceOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;


@Service
@RequiredArgsConstructor
public class RedisMessageReceiver implements MessageListener {

    private final JsonMapper jsonMapper;
    private final WebSocketSessionRegistry registry;
    private final ChatServiceOrchestrator chatService;

    /**
     * This is the listener of redis, if this recipient websocket is associated with this service, it will redirect the message
     * otherwise it will discard the message.
     * @param message message must not be {@literal null}.
     * @param pattern pattern matching the channel (if specified) - can be {@literal null}.
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        var json = new String(message.getBody());
        ChatMessagePayload messageDto = jsonMapper.readValue(json, ChatMessagePayload.class);

        if (registry.getSession(messageDto.recipientId()).isPresent()) {
            chatService.internalSendPrivateMessage(messageDto.recipientId(), messageDto);
        }


    }
}
