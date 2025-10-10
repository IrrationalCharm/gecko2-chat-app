package eu.irrationalcharm.messagepersistenceservice.listener;


import eu.irrationalcharm.events.MessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageListener {

    @KafkaListener(topics = "${spring.kafka.topic.user-messages}")
    public void userMessageConsumerListener(MessageEvent messageEvent) {
        System.out.println("It works!! :)) "+ messageEvent);

    }
}
