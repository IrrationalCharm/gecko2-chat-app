package eu.irrationalcharm.userservice.service.event;

import eu.irrationalcharm.events.UserUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventProducer {

    @Value("${spring.kafka.topic.user-updates}")
    private String userUpdateTopic;

    private final KafkaTemplate<String, UserUpdateEvent> kafkaTemplate;


    //Used to notify messaging-service to evict the cache as the user has been updated
    public void publishUserUpdatedEvent(@NotNull UserUpdateEvent... userUpdateEvents) {
        for(UserUpdateEvent updateEvent : userUpdateEvents) {
            log.debug("Sending Kafka event to notify messaging-service to evict data by user {}", updateEvent.userId());
            kafkaTemplate.send(userUpdateTopic, updateEvent.userId(), updateEvent);
        }
    }
}
