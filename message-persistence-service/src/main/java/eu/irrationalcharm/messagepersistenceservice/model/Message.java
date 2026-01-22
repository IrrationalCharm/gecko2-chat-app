package eu.irrationalcharm.messagepersistenceservice.model;

import eu.irrationalcharm.enums.TextType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


@Getter @Setter
@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    private String clientMsgId; //ID assigned by client
    @Indexed
    private String conversationId; // combination of two uuid separated by ":", uuid ordered by size.

    private String senderId;
    private String content;

    @Indexed
    private Instant timestamp;
    private TextType textType;

}
