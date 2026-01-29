package eu.irrationalcharm.messaging_service.service.event;

import eu.irrationalcharm.events.chat.ChatEvent;
import eu.irrationalcharm.events.chat.MessageEvent;
import eu.irrationalcharm.messaging_service.dto.request.SendMessageRequest;
import eu.irrationalcharm.messaging_service.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageEventProducer {

    @Value("${spring.kafka.topic.chat-events}")
    private String chatEventsTopic;

    private final KafkaTemplate<String, ChatEvent> kafkaTemplate;

    public void produceMessageEvent(ChatEvent event) {
        kafkaTemplate.send(chatEventsTopic, event.conversationId(), event); //THe second field guarantees order for each conversation

        log.info("A Chat event has been sent to Kafka");
    }

}
