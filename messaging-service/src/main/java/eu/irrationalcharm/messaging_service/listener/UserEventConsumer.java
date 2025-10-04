package eu.irrationalcharm.messaging_service.listener;


import eu.irrationalcharm.messaging_service.event.UserUpdateEvent;
import eu.irrationalcharm.messaging_service.service.UserUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventConsumer {

    private final UserUpdateService updateService;

    @KafkaListener(topics = "${spring.kafka.topic.user-updates}")
    public void userUpdateEventConsumerListener(UserUpdateEvent updateEvent) {
        log.info("User update event, evicting cached data from {}", updateEvent);

        updateService.evictUserGraph(updateEvent.username(), updateEvent.providerId());
    }

}
