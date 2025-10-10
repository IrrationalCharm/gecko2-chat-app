package eu.irrationalcharm.messagepersistenceservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LastMessage {

    private String senderId;
    private String content;
    private String timestamp;
}
