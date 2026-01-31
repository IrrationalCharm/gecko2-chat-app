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
    private String id; //user id of the two participants of the conversation separated by :

    private Set<String> participants = new HashSet<>();

    //Key = UserID of the person who READ the messages.
    //Value = Timestamp of the last message they read.
    private Map<String, Instant> readCursors = new HashMap<>();

    //Key = UserID of the person who RECEIVED the messages.
    //Value = Timestamp of the last message delivered to their device.
    private Map<String, Instant> deliveryCursors = new HashMap<>();

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    private LastMessage lastMessage;
}
