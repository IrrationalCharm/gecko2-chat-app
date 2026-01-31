package eu.irrationalcharm.messagepersistenceservice.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter @Setter
@Document(collection = "conversations")
public class Conversation {

    @Id
    private String id;

    private Set<String> participants = new HashSet<>();

    // Key: UserId, Value: MessageId of the last message they READ
    private Map<String, Instant> lastReadTimestamps = new HashMap<>();

    // Key: UserId, Value: MessageId of the last message they DELIVERED. User sends a message, when the message was finally delivered. the recipient of the message updates this with the id of the sender of the message
    private Map<String, Instant> lastDeliveredTimestamps = new HashMap<>();

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    private LastMessage lastMessage;
}
