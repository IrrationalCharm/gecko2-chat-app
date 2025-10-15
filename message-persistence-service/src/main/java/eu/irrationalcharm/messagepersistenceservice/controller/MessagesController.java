package eu.irrationalcharm.messagepersistenceservice.controller;


import eu.irrationalcharm.messagepersistenceservice.dto.ConversationSummaryDto;

import eu.irrationalcharm.messagepersistenceservice.dto.response.ApiResponse;
import eu.irrationalcharm.messagepersistenceservice.dto.response.SuccessResponseDto;
import eu.irrationalcharm.messagepersistenceservice.enums.SuccessfulCode;
import eu.irrationalcharm.messagepersistenceservice.service.RetrieveChatHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class MessagesController {

    private final RetrieveChatHistoryService retrieveHistoryService;

    @GetMapping("/last-messages")
    public ResponseEntity<SuccessResponseDto<List<ConversationSummaryDto>>> lastMessages(
            Authentication authentication,
            HttpServletRequest request) {

        List<ConversationSummaryDto> chatHistorySet = retrieveHistoryService.fetchLastMessages(authentication);

        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.CHATS_FOUND,
                String.format("Successfully removed %s as friend", ""),
                chatHistorySet,
                request
        );
    }
}
