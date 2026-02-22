package eu.irrationalcharm.userservice.service.event;

import eu.irrationalcharm.events.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {

    @Value("${spring.kafka.topic.notification-update}")
    private String topic;

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public void publishNotificationEvent(NotificationEvent event) {
        log.debug("Sending Kafka event to notify messaging-service of a user update event: {}", event.type());
        kafkaTemplate.send(topic, event);
    }
}
