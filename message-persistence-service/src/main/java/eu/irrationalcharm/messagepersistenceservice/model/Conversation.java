package eu.irrationalcharm.messagepersistenceservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter @Setter
@Document(collection = "conversations")
public class Conversation {

    @Id
    private String id;

    private String[] participants;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LastMessage lastMessage;
}
