package eu.irrationalcharm.messagepersistenceservice.model;

import eu.irrationalcharm.kafka.enums.TextType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Getter @Setter
@Document(collection = "messages")
public class Message {

    @Id
    private String id;
    @Indexed
    private String conversationId;

    private String senderId;
    private String content;

    @Indexed
    private LocalDateTime timestamp;
    private TextType textType;

}
