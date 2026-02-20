package eu.irrationalcharm.mobilebff.client;

import eu.irrationalcharm.dto.persistence_service.MessageHistoryDto;
import eu.irrationalcharm.dto.response.SuccessResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Collections;
import java.util.List;


@HttpExchange(url = "${url.persistence-service}", accept = "application/json")
public interface PersistenceServiceClient {

    @Retry(name = "sync-conversation")
    @CircuitBreaker(name = "sync-conversation", fallbackMethod = "getSyncConversationFallback")
    @GetExchange("/v2/chat/sync")
    ResponseEntity<SuccessResponseDto<List<MessageHistoryDto>>> getSyncConversation(@RequestParam Long sinceTimestamp);

    default ResponseEntity<SuccessResponseDto<List<MessageHistoryDto>>> getSyncConversationFallback(Throwable t) {
        return ResponseEntity.ok(SuccessResponseDto.<List<MessageHistoryDto>>builder()
                .status(200)
                .code("FALLBACK_SYNC_CONVERSATION") // Optional: a custom code to let the frontend know this is cached/fallback data
                .detail("Could not retrieve messages at this time.")
                .data(Collections.emptyList())
                .build());
    }
}
