package eu.irrationalcharm.messagepersistenceservice.migration;


import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@ChangeUnit(id = "addIndexToConversations", order = "003", author = "Dominik")
public class AddIndexToConversations {

    private final MongoTemplate mongoTemplate;

    public AddIndexToConversations(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Execution
    public void migration() {
        mongoTemplate.indexOps("conversations").createIndex(new Index()
                .on("participants", Sort.Direction.ASC)
                .on("updatedAt", Sort.Direction.DESC));
    }

    @RollbackExecution
    public void rollback() {
        if(mongoTemplate.collectionExists("conversations"))
            mongoTemplate.dropCollection("conversations");
    }
}
