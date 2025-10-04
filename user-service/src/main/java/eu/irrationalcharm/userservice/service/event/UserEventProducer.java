package eu.irrationalcharm.userservice.service.event;

import eu.irrationalcharm.userservice.event.UserUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserEventProducer {

    @Value("${spring.kafka.topic.user-updates}")
    private String userUpdateTopic;

    private final KafkaTemplate<String, UserUpdateEvent> kafkaTemplate;

    @Transactional(readOnly = true)
    public void publishUserUpdatedEvent(@NotNull UserUpdateEvent... userUpdateEvents) {
        for(UserUpdateEvent updateEvent : userUpdateEvents) {
            kafkaTemplate.send(userUpdateTopic, updateEvent.username(), updateEvent);
        }
    }
}
