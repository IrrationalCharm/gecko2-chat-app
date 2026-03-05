package eu.irrationalcharm.userservice.listener;


import eu.irrationalcharm.events.media_service.ProfilePictureUpdatedEvent;
import eu.irrationalcharm.userservice.service.UserService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ProfilePictureUpdateEventListener {

    private final UserService userService;

    @RetryableTopic(
            attempts = "5",
            exclude = {ConstraintViolationException.class},
            backOff = @BackOff(delay = 1000, maxDelay = 10000, multiplier = 2)
    )
    @KafkaListener(topics = "${spring.kafka.topic.profile-image}")
    public void userMessageConsumerListener(@Valid ProfilePictureUpdatedEvent event) {
        log.info("Profile picture update event received: {}", event);

        UUID uuid = UUID.fromString(event.userId());

        userService.updateProfileImageUrl(uuid, event.fullUrl());

    }

    @DltHandler
    public void dltHandler(ProfilePictureUpdatedEvent event,
                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("Event failed processing and was sent to DLT. Topic: {}, Event: {}", topic, event);
        // You can add logic here to alert admins, save to a DB table for review, etc.
    }
}
