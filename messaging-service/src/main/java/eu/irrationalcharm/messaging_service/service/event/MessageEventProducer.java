package eu.irrationalcharm.messaging_service.service.event;

import eu.irrationalcharm.events.MessageEvent;
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

    @Value("${spring.kafka.topic.user-messages}")
    private String userMessageTopic;

    private final KafkaTemplate<String, MessageEvent> kafkaTemplate;

    public void produceMessageEvent(MessageEvent messageEvent) {
        kafkaTemplate.send(userMessageTopic, messageEvent);
        log.info("A message has been sent to kafka");
    }

    public void produceMessageEvent(SendMessageRequest chatMessageDto) {
        MessageEvent messageEvent = MessageMapper.mapToMessageEvent(chatMessageDto);
        kafkaTemplate.send(userMessageTopic, messageEvent);
    }




}
