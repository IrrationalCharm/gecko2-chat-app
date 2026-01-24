package eu.irrationalcharm.messagepersistenceservice.repository;

import eu.irrationalcharm.messagepersistenceservice.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;


public interface MessageRepository extends MongoRepository<Message, String> {

    //Returns all messages after the timestamp in descending order
    List<Message> findByConversationIdAndTimestampIsAfterOrderByTimestampDesc(String conversationId, Instant timestamp);

    Page<Message> findByConversationIdOrderByTimestampDesc(String conversationId, Pageable pageable);

    Slice<Message> findByConversationIdAndTimestampLessThanOrderByTimestampDesc(String conversationId, Instant timestamp, Pageable pageable);
}
