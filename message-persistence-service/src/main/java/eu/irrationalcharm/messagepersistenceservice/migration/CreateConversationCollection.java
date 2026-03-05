package eu.irrationalcharm.messagepersistenceservice.migration;


import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id = "createConversationCollection",order = "002", author = "Dominik")
public class CreateConversationCollection {

    private final MongoTemplate mongoTemplate;

    public CreateConversationCollection(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void migration() {
        if(!mongoTemplate.collectionExists("conversations"))
            mongoTemplate.collectionExists("conversations");
    }

    @RollbackExecution
    public void rollback() {
        if(mongoTemplate.collectionExists("conversations"))
            mongoTemplate.dropCollection("conversations");
    }

}
