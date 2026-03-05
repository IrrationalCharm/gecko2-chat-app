package eu.irrationalcharm.mediaservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {


    @Value("${spring.kafka.topic.profile-image}")
    private String topic;

    @Bean
    public NewTopic newTopic() {
        return TopicBuilder.name(topic)
                .partitions(1)
                .build();
    }
}
