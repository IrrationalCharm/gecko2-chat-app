package eu.irrationalcharm.mediaservice.service.event;

import eu.irrationalcharm.events.media_service.ProfilePictureUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfilePictureProducer {

    @Value("${spring.kafka.topic.profile-image}")
    private String topic;

    private final KafkaTemplate<String, ProfilePictureUpdatedEvent> kafkaTemplate;

    public void publishProfilePictureUpdateEvent(ProfilePictureUpdatedEvent event) {
        kafkaTemplate.send(topic, event);
        log.debug("Kafka ProfilePictureUpdatedEvent event produced");
    }
}
