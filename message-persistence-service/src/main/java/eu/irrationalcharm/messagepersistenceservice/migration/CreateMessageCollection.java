package eu.irrationalcharm.messagepersistenceservice.migration;


import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@ChangeUnit(id="createMessageCollection", order = "001", author = "Dominik")
public class CreateMessageCollection {

    private final MongoTemplate mongoTemplate;

    public CreateMessageCollection(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void migrationMethod() {
        if(!mongoTemplate.collectionExists("messages")) {
            mongoTemplate.createCollection("messages");
            mongoTemplate.indexOps("messages").createIndex(new Index()
                    .on("conversationId", Sort.Direction.ASC)
                    .on("deliveryTimestamp", Sort.Direction.DESC));
        }
    }

    @RollbackExecution
    public void rollback() {
        if(mongoTemplate.collectionExists("messages")) {
            mongoTemplate.dropCollection("messages");
        }
    }
}
