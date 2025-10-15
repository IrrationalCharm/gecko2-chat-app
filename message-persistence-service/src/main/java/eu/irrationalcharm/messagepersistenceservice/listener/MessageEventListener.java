package eu.irrationalcharm.messagepersistenceservice.listener;


import eu.irrationalcharm.kafka.events.MessageEvent;
import eu.irrationalcharm.messagepersistenceservice.service.PersistMessageService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MessageEventListener {

    private final PersistMessageService persistMessageService;

    @RetryableTopic(
            attempts = "5",
            exclude = {ConstraintViolationException.class},
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    @KafkaListener(topics = "${spring.kafka.topic.user-messages}")
    public void userMessageConsumerListener(@Valid MessageEvent messageEvent) {
        log.info("Message received: {}", messageEvent);

        persistMessageService.persistMessage(messageEvent);

    }


    @DltHandler
    public void dltHandler(ConsumerRecord<String, byte[]> record) {
        log.error("Failed to process message from topic {} partition {} at offset {}",
                record.topic(), record.partition(), record.offset());
    }
}
