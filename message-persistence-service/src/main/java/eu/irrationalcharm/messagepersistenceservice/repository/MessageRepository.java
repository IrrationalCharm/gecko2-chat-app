package eu.irrationalcharm.messagepersistenceservice.repository;

import eu.irrationalcharm.messagepersistenceservice.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface MessageRepository extends MongoRepository<Message, String> {

    Page<Message> findByIdOrderByTimestampDesc(String id, Pageable pageable);

    Page<Message> findByConversationIdOrderByTimestampDesc(String conversationId, PageRequest messagePage);
}
