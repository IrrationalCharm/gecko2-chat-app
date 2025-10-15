package eu.irrationalcharm.messagepersistenceservice.service;


import eu.irrationalcharm.messagepersistenceservice.dto.ConversationSummaryDto;
import eu.irrationalcharm.messagepersistenceservice.dto.MessageDto;
import eu.irrationalcharm.messagepersistenceservice.dto.MessageHistoryDto;
import eu.irrationalcharm.messagepersistenceservice.mapper.ConversationMapper;
import eu.irrationalcharm.messagepersistenceservice.mapper.MessageMapper;
import eu.irrationalcharm.messagepersistenceservice.model.Conversation;
import eu.irrationalcharm.messagepersistenceservice.model.Message;
import eu.irrationalcharm.messagepersistenceservice.repository.ConversationRepository;
import eu.irrationalcharm.messagepersistenceservice.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RetrieveChatHistoryService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    @PreAuthorize("authenticated()")
    public List<ConversationSummaryDto> fetchLastMessages(Authentication authentication) {
        List<Conversation> conversations = conversationRepository.findByParticipantsContainsOrderByUpdatedAtDesc(authentication.getName());
        if (conversations.isEmpty())
            return Collections.emptyList();

        List<ConversationSummaryDto> conversationDto = new ArrayList<>(conversations.size());

        conversations.forEach(conv ->
            conversationDto.add(ConversationMapper.mapToDto(conv)));

        return conversationDto;
    }


    @PreAuthorize("authenticated()")
    public List<MessageHistoryDto> fetchRecentMessages(int page, int size, Authentication authentication) {

        PageRequest conversationPage = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        List<Conversation> conversations = conversationRepository.findByParticipantsContainsOrderByUpdatedAtDesc(authentication.getName(), conversationPage);

        if(conversations.isEmpty())
            return Collections.emptyList();


        PageRequest messagePageRequest = PageRequest.of(0, 20, Sort.by("timestamp").descending());
        List<MessageHistoryDto> paginatedConversations = new ArrayList<>();

        for (Conversation conv : conversations) {
            Page<Message> messagesPage = messageRepository.findByConversationIdOrderByTimestampDesc(conv.getId(), messagePageRequest);
            List<MessageDto> messageDtoList = new ArrayList<>();
            messagesPage.getContent().forEach(message ->
                    messageDtoList.add(MessageMapper.mapToDto(message)));

            var messageHistoryDto = MessageHistoryDto.builder()
                    .conversationId(conv.getId())
                    .messages(messageDtoList)
                    .pageNumber(messagesPage.getNumber())
                    .totalPages(messagesPage.getTotalPages())
                    .isLastPage(messagesPage.isLast())
                    .build();

            paginatedConversations.add(messageHistoryDto);
        }

        return paginatedConversations;
    }

    public List<MessageHistoryDto> getConversation(int page, int size, String friendId, Authentication authentication) {
        //Page<Message> messagesPage = messageRepository.findByConversationIdOrderByTimestampDesc(conv.getId(), messagePageRequest);

        return null;
    }
}
