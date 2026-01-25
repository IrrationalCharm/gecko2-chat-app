package eu.irrationalcharm.userservice.service.event;

import eu.irrationalcharm.events.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationProducer {

    @Value("${spring.kafka.topic.notification-update}")
    private String topic;

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public void publishNotificationEvent(NotificationEvent event) {
        kafkaTemplate.send(topic, event);
    }
}
