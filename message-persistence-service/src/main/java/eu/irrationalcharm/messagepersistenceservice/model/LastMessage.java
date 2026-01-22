package eu.irrationalcharm.messagepersistenceservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter @Setter
@AllArgsConstructor
public class LastMessage {

    private String senderId;
    private String content;
    private Instant timestamp;
}
