package eu.irrationalcharm.userservice.adapter.in.messaging;

import eu.irrationalcharm.events.media_service.ProfilePictureUpdatedEvent;
import eu.irrationalcharm.userservice.application.port.in.UpdateUserUseCase;
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
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Slf4j
@Component
@Validated
@RequiredArgsConstructor
public class ProfilePictureUpdateListener {

    private final UpdateUserUseCase updateUserUseCase;

    @RetryableTopic(
            attempts = "5",
            exclude = {ConstraintViolationException.class},
            backOff = @BackOff(delay = 1000, maxDelay = 10000, multiplier = 2)
    )
    @KafkaListener(topics = "${spring.kafka.topic.profile-image}")
    public void userMessageConsumerListener(@Valid ProfilePictureUpdatedEvent event) {
        log.info("Profile picture update event received: {}", event);

        UUID userId = UUID.fromString(event.userId());
        updateUserUseCase.updateProfileImageUrl(userId, event.fullUrl());
    }

    @DltHandler
    public void dltHandler(ProfilePictureUpdatedEvent event,
                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("Event failed processing and was sent to DLT. Topic: {}, Event: {}", topic, event);
    }
}
