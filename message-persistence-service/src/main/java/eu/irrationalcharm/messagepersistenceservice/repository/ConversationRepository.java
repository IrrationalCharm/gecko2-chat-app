package eu.irrationalcharm.messagepersistenceservice.repository;

import eu.irrationalcharm.messagepersistenceservice.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConversationRepository extends MongoRepository<Conversation, String> {


    List<Conversation> findByParticipantsContainsOrderByUpdatedAtDesc(String name);
}
