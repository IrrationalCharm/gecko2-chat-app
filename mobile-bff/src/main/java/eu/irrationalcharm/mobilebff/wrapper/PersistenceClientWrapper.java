package eu.irrationalcharm.mobilebff.wrapper;

import eu.irrationalcharm.dto.persistence_service.MessageHistoryDto;
import eu.irrationalcharm.mobilebff.client.PersistenceServiceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersistenceClientWrapper {

    private final PersistenceServiceClient persistenceServiceClient;

    @Retry(name = "sync-conversation")
    @CircuitBreaker(name = "sync-conversation", fallbackMethod = "getSyncConversationFallback")
    public List<MessageHistoryDto> getSyncConversation(Long sinceTimestamp) {
        return persistenceServiceClient.getSyncConversation(sinceTimestamp).getBody().data();
    }


    public List<MessageHistoryDto> getSyncConversationFallback(Long sinceTimestamp, Throwable t) {
        log.warn("Fallback triggered for sync conversation: {}", t.getMessage());
        return Collections.emptyList();
    }
}
