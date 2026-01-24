package eu.irrationalcharm.messagepersistenceservice.controller;


import eu.irrationalcharm.dto.persistence_service.ConversationSummaryDto;
import eu.irrationalcharm.dto.persistence_service.MessageHistoryDto;
import eu.irrationalcharm.dto.response.SuccessResponseDto;
import eu.irrationalcharm.enums.SuccessfulCode;
import eu.irrationalcharm.messagepersistenceservice.dto.response.ApiResponse;
import eu.irrationalcharm.messagepersistenceservice.service.RetrieveChatHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v2/chat")
@RequiredArgsConstructor
public class MessagesControllerV2 {

    private final RetrieveChatHistoryService retrieveHistoryService;


    @GetMapping("/last-messages")
    public ResponseEntity<SuccessResponseDto<List<ConversationSummaryDto>>> lastMessages(
            Authentication authentication,
            HttpServletRequest request) {

        List<ConversationSummaryDto> chatHistorySet = retrieveHistoryService.fetchLastMessages(authentication);

        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.CHATS_FOUND,
                "Successfully retrieve last messages from friends",
                chatHistorySet,
                request
        );
    }


    /**
     * Returns a list of conversations with messages starting from timestamp since onwards, if no value is provided, it
     * fetches the 20 most recent conversations with 20 messages per conversation
     * @param sinceTimestamp messages to return from this timestamp
     */
    @GetMapping("/sync")
    public ResponseEntity<SuccessResponseDto<List<MessageHistoryDto>>> syncMessages(
            @RequestParam(required = false) Long sinceTimestamp,
            Authentication authentication,
            HttpServletRequest request) {

        List<MessageHistoryDto> messageHistoryList;

        if(sinceTimestamp != null && sinceTimestamp > 0) {
            messageHistoryList = retrieveHistoryService.syncMessages(sinceTimestamp, authentication);
        } else
            messageHistoryList = retrieveHistoryService.fetchRecentMessages(0, 20, authentication);

        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.CHATS_FOUND,
                "Successfully retrieve last messages from friends",
                messageHistoryList,
                request
        );
    }


    /**
     * @param page page of the conversations
     * @param size number of conversations per page
     * @return This returns a list of conversations, 20 messages per conversation
     */
    @GetMapping("/conversations/hydrated")
    public ResponseEntity<SuccessResponseDto<List<MessageHistoryDto>>> getHydratedConversations(
            @RequestParam(defaultValue = "0") @Min(value = 0) int page,
            @RequestParam(defaultValue = "10") @Min(value = 1) @Max(value = 50) int size,
            Authentication authentication,
            HttpServletRequest request) {

        List<MessageHistoryDto> chatHistoryList = retrieveHistoryService.fetchRecentMessages(page, size, authentication);

        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.CHATS_FOUND,
                "Successfully retrieved recent messages from recent conversations",
                chatHistoryList,
                request
        );
    }


    //Get messages by epoch time and not by page
    @GetMapping("/conversation/{friendId}")
    public ResponseEntity<SuccessResponseDto<MessageHistoryDto>> getConversation(
            @RequestParam long before,
            @RequestParam(defaultValue = "1") @Min(value = 1) int size,
            @PathVariable String friendId,
            Authentication authentication,
            HttpServletRequest request) {


        MessageHistoryDto chatHistorySet = retrieveHistoryService.getConversation(before, size, friendId, authentication);

        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.CHATS_FOUND,
                "Successfully retrieved recent messages from recent conversations",
                chatHistorySet,
                request
        );
    }

}
