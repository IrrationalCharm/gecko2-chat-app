package eu.irrationalcharm.messagepersistenceservice.controller;


import eu.irrationalcharm.messagepersistenceservice.dto.ConversationSummaryDto;

import eu.irrationalcharm.messagepersistenceservice.dto.MessageHistoryDto;
import eu.irrationalcharm.messagepersistenceservice.dto.response.ApiResponse;
import eu.irrationalcharm.messagepersistenceservice.dto.response.SuccessResponseDto;
import eu.irrationalcharm.messagepersistenceservice.enums.SuccessfulCode;
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
                "Successfully retrieve last messages from friends",
                chatHistorySet,
                request
        );
    }


    @GetMapping("/conversations/hydrated")
    public ResponseEntity<SuccessResponseDto<List<MessageHistoryDto>>> getHydratedConversations(
            @RequestParam(defaultValue = "0") @Min(value = 0) int page,
            @RequestParam(defaultValue = "10") @Min(value = 1) @Max(value = 50) int size,
            Authentication authentication,
            HttpServletRequest request) {

        List<MessageHistoryDto> chatHistorySet = retrieveHistoryService.fetchRecentMessages(page, size, authentication);

        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.CHATS_FOUND,
                "Successfully retrieved recent messages from recent conversations",
                chatHistorySet,
                request
        );
    }


    @GetMapping("/conversation/{friendId}")
    public ResponseEntity<SuccessResponseDto<List<MessageHistoryDto>>> getConversation(
            @RequestParam(defaultValue = "0") @Min(value = 0) int page,
            @RequestParam(defaultValue = "10") @Min(value = 1) @Max(value = 100) int size,
            @PathVariable String friendId,
            Authentication authentication,
            HttpServletRequest request) {


        List<MessageHistoryDto> chatHistorySet = retrieveHistoryService.getConversation(page, size, friendId, authentication);

        return ApiResponse.success(
                HttpStatus.OK,
                SuccessfulCode.CHATS_FOUND,
                "Successfully retrieved recent messages from recent conversations",
                chatHistorySet,
                request
        );
    }

}
