package eu.irrationalcharm.messagepersistenceservice.service;


import eu.irrationalcharm.dto.persistence_service.ConversationSummaryDto;
import eu.irrationalcharm.dto.persistence_service.MessageDto;
import eu.irrationalcharm.dto.persistence_service.MessageHistoryDto;
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

import java.util.Collections;
import java.util.List;

import static eu.irrationalcharm.messagepersistenceservice.utils.ConversationUtils.generateConversationId;

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

        return conversations.stream()
                .map(ConversationMapper::mapToDto)
                .toList();
    }


    @PreAuthorize("authenticated()")
    public List<MessageHistoryDto> fetchRecentMessages(int page, int size, Authentication authentication) {

        PageRequest conversationPage = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        List<Conversation> conversations = conversationRepository.findByParticipantsContainsOrderByUpdatedAtDesc(authentication.getName(), conversationPage);

        if(conversations.isEmpty())
            return Collections.emptyList();

        PageRequest messagePageRequest = PageRequest.of(0, 20, Sort.by("timestamp").descending());

        return conversations.stream()
                .map(conv -> fetchMessageAndMapToDto(conv.getId(), messagePageRequest))
                .toList();
    }


    @PreAuthorize("authenticated()")
    public MessageHistoryDto getConversation(int page, int size, String friendId, Authentication authentication) {
        String conversationId = generateConversationId(authentication.getName(), friendId);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());

        return fetchMessageAndMapToDto(conversationId, pageRequest);
    }



    private MessageHistoryDto fetchMessageAndMapToDto(String conversationId, PageRequest pageRequest) {
        Page<Message> messagePage = messageRepository.findByConversationIdOrderByTimestampDesc(conversationId, pageRequest);

        List<MessageDto> messageDtoList = messagePage.getContent().stream()
                .map(MessageMapper::mapToDto)
                .toList();

        return new MessageHistoryDto(
                conversationId,
                messageDtoList,
                messagePage.getNumber(),
                messagePage.getTotalPages(),
                messagePage.isLast()
        );
    }
}
