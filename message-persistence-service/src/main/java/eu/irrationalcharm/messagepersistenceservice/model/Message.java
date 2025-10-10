package eu.irrationalcharm.messagepersistenceservice.model;

import eu.irrationalcharm.messagepersistenceservice.enums.TextType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "messages")
public class Message {

    @Id
    private String id;
    private String conversationId;
    private String senderId;
    private String content;
    private LocalDateTime timestamp;
    private TextType textType;

}
