package eu.irrationalcharm.messagepersistenceservice.config;

import org.apache.kafka.common.errors.RecordDeserializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaOperations<String, Object> template) {
        //Publishes failed records to a dead-letter topic
        var recoverer = new DeadLetterPublishingRecoverer(template);

        //Create an error handler that doesn't retry at all the specified exceptions
        var errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 0L));

        errorHandler.addNotRetryableExceptions(
                DeserializationException.class,
                RecordDeserializationException.class
        );

        return errorHandler;
    }

}
