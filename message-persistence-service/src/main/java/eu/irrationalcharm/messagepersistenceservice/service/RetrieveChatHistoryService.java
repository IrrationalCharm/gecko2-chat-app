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
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static eu.irrationalcharm.messagepersistenceservice.utils.ConversationUtils.generateConversationId;

@Service
@RequiredArgsConstructor
public class RetrieveChatHistoryService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    /**
     * Finds all conversations and returns ConversationSummaryDto, which contains the last message sent in the conversation
     */
    @PreAuthorize("authenticated()")
    public List<ConversationSummaryDto> fetchLastMessages(Authentication authentication) {
        List<Conversation> conversations = conversationRepository.findByParticipantsContainsOrderByUpdatedAtDesc(authentication.getName());
        if (conversations.isEmpty())
            return Collections.emptyList();

        return conversations.stream()
                .map(ConversationMapper::mapToDto)
                .toList();
    }


    /**
     * @param before epoch time, fetch messages before given epoch time
     * @param size number of messages to be retrieved
     * @param friendId chat to be retrieved
     * @param authentication authentication
     * @return messages
     */
    @PreAuthorize("authenticated()")
    public MessageHistoryDto getConversation(long before, int size, String friendId, Authentication authentication) {
        String conversationId = generateConversationId(authentication.getName(), friendId);
        Instant dateBefore = Instant.ofEpochMilli(before);

        Pageable pageable = PageRequest.of(0, size);

        return fetchMessageAndMapToDto(conversationId, dateBefore, pageable);
    }



    private MessageHistoryDto fetchMessageAndMapToDto(String conversationId, Instant dateBefore, Pageable pageable) {
        Slice<Message> messageSlice = messageRepository.findByConversationIdAndTimestampLessThanOrderByTimestampDesc(conversationId, dateBefore, pageable);

        List<MessageDto> messageDtoList = messageSlice.getContent().stream()
                .map(MessageMapper::mapToDto)
                .toList();

        return new MessageHistoryDto(
                conversationId,
                messageDtoList,
                messageSlice.getNumber(),
                0,
                messageSlice.isLast()
        );
    }


    private MessageHistoryDto fetchMessageAndMapToDto(String conversationId, Pageable pageRequest) {
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
    public List<MessageHistoryDto> syncMessages(Long sinceTimestamp, Authentication authentication) {
        Instant sinceInstant = Instant.ofEpochMilli(sinceTimestamp);
        List<Conversation> conversations = conversationRepository.findByParticipantsContainsAndUpdatedAtAfter(authentication.getName(), sinceInstant);

        if (conversations.isEmpty())
            return Collections.emptyList();

        return conversations.stream()
                .map(conv -> fetchMessagesAfterTimestampAndMapToDto(conv.getId(), sinceInstant))
                .toList();
    }


    //Returns all messages after the timestamp in descending order and maps to Dto
    private MessageHistoryDto fetchMessagesAfterTimestampAndMapToDto(String conversationId, Instant sinceInstant) {

        List<Message> messages = messageRepository.findByConversationIdAndTimestampIsAfterOrderByTimestampDesc(conversationId, sinceInstant);

        List<MessageDto> messageDtos = messages.stream()
                .map(MessageMapper::mapToDto)
                .toList();

        return new MessageHistoryDto(
                conversationId,
                messageDtos,
                0,
                0,
                true
        );
    }


    @Deprecated
    @PreAuthorize("authenticated()")
    public MessageHistoryDto getConversation(int page, int size, String friendId, Authentication authentication) {
        String conversationId = generateConversationId(authentication.getName(), friendId);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());

        return fetchMessageAndMapToDto(conversationId, pageRequest);
    }


}
