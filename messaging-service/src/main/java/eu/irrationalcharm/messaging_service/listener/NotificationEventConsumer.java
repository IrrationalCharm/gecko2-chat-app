package eu.irrationalcharm.messaging_service.listener;

import eu.irrationalcharm.events.NotificationEvent;
import eu.irrationalcharm.messaging_service.service.orchestrator.NotificationServiceOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationServiceOrchestrator notificationOrchestrator;

    @KafkaListener(topics = {"${spring.kafka.topic.notification-update}"})
    public void notificationEventConsumerListener(NotificationEvent notificationEvent) {
        log.info("Received {} notification from user-service", notificationEvent.type());

        notificationOrchestrator.receivedNotification(notificationEvent);

    }
}
