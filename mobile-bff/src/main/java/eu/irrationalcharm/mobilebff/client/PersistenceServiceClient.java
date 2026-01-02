package eu.irrationalcharm.mobilebff.client;

import eu.irrationalcharm.dto.persistence_service.ConversationSummaryDto;
import eu.irrationalcharm.dto.response.SuccessResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange(url = "/message-persistence-service", accept = "application/json")
public interface PersistenceServiceClient {

    @GetExchange("/last-messages")
    ResponseEntity<SuccessResponseDto<List<ConversationSummaryDto>>> getSummaryMessages();
}
