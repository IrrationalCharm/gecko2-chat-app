package eu.irrationalcharm.messagepersistenceservice.model;

import eu.irrationalcharm.enums.TextType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter @Setter
@AllArgsConstructor
public class LastMessage {

    private String clientMsgId;
    private String senderId;
    private String content;
    private Instant timestamp;
    private TextType textType;
}
