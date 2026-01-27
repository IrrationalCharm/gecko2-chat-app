package eu.irrationalcharm.mobilebff.client;

import eu.irrationalcharm.dto.persistence_service.ConversationSummaryDto;
import eu.irrationalcharm.dto.persistence_service.MessageHistoryDto;
import eu.irrationalcharm.dto.response.SuccessResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange(url = "${url.persistence-service}", accept = "application/json")
public interface PersistenceServiceClient {

    @GetExchange("/chat/last-messages")
    ResponseEntity<SuccessResponseDto<List<ConversationSummaryDto>>> getSummaryMessages();

    @GetExchange("/v2/chat/sync")
    ResponseEntity<SuccessResponseDto<List<MessageHistoryDto>>> getSyncConversation(@RequestParam Long sinceTimestamp);
}
