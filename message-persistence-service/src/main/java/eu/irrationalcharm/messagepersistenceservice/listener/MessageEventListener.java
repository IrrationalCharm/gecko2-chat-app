package eu.irrationalcharm.messagepersistenceservice.listener;


import eu.irrationalcharm.events.MessageEvent;
import eu.irrationalcharm.messagepersistenceservice.service.PersistMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageEventListener {

    private final PersistMessageService persistMessageService;

    @KafkaListener(topics = "${spring.kafka.topic.user-messages}")
    public void userMessageConsumerListener(MessageEvent messageEvent) {
        log.info("Message received: {}", messageEvent);

        persistMessageService.persistMessage(messageEvent);

    }
}
