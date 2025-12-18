package eu.irrationalcharm.messagepersistenceservice;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongock
@EnableMongoRepositories
@EnableMongoAuditing
@SpringBootApplication
public class MessagePersistenceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessagePersistenceServiceApplication.class, args);
    }

}
