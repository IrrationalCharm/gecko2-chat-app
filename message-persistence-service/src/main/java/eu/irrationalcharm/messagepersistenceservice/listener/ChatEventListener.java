package eu.irrationalcharm.messagepersistenceservice.listener;


import eu.irrationalcharm.events.chat.ChatEvent;
import eu.irrationalcharm.events.chat.MessageEvent;
import eu.irrationalcharm.events.chat.MsgDeliveredEvent;
import eu.irrationalcharm.events.chat.MsgReadEvent;
import eu.irrationalcharm.messagepersistenceservice.service.PersistMessageService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ChatEventListener {

    private final PersistMessageService persistMessageService;

    @RetryableTopic(
            attempts = "5",
            exclude = {ConstraintViolationException.class},
            backOff = @BackOff(delay = 1000, maxDelay = 10000, multiplier = 2)
    )
    @KafkaListener(topics = "${spring.kafka.topic.chat-events}")
    public void userMessageConsumerListener(@Valid ChatEvent event) {
        log.info("Chat event received: {}", event);

        switch (event) {
            case MessageEvent messageEvent ->  persistMessageService.persistMessage(messageEvent);
            case MsgDeliveredEvent msgDeliveredEvent -> persistMessageService.updateDeliveryStatus(msgDeliveredEvent);
            case MsgReadEvent msgReadEvent -> persistMessageService.updateReadStatus(msgReadEvent);
        }

    }


    @DltHandler
    public void dltHandler(ConsumerRecord<String, byte[]> record) {
        log.error("Failed to process message from topic {} partition {} at offset {}",
                record.topic(), record.partition(), record.offset());
    }
}
