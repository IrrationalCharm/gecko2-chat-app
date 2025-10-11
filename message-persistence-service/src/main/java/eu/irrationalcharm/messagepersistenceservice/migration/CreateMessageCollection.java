package eu.irrationalcharm.messagepersistenceservice.migration;


import eu.irrationalcharm.messagepersistenceservice.model.Message;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@ChangeUnit(id="createMessageCollection", order = "001")
public class CreateMessageCollection {


    @Execution
    public void migrationMethod(MongoTemplate mongoTemplate) {
        if(!mongoTemplate.collectionExists("messages")) {
            mongoTemplate.createCollection("messages");
            mongoTemplate.indexOps("messages").createIndex(new Index()
                    .on("conversationId", Sort.Direction.ASC)
                    .on("timestamp", Sort.Direction.DESC));
        }
    }
}
