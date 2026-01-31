package eu.irrationalcharm.messagepersistenceservice.service;


import eu.irrationalcharm.dto.persistence_service.MessageDto;
import eu.irrationalcharm.dto.persistence_service.MessageHistoryDto;
import eu.irrationalcharm.enums.MessageStatus;
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
     * @param before epoch time, fetch messages before given epoch time
     * @param size number of messages to be retrieved
     * @param friendId chat to be retrieved
     * @param authentication authentication
     * @return messages
     */
    @PreAuthorize("authenticated()")
    public MessageHistoryDto getConversation(long before, int size, String friendId, Authentication authentication) {
        String currentUserId = authentication.getName();
        String conversationId = generateConversationId(currentUserId, friendId);
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found for ID: " + conversationId));

        Instant dateBefore = Instant.ofEpochMilli(before);
        Pageable pageable = PageRequest.of(0, size);
        Slice<Message> messageSlice = messageRepository.findByConversationIdAndTimestampLessThanOrderByTimestampDesc(conversationId, dateBefore, pageable);


        return buildHistoryDto(conv, messageSlice.getContent(), currentUserId, messageSlice.isLast(), 0);
    }


    @PreAuthorize("authenticated()")
    public List<MessageHistoryDto> fetchRecentMessages(int page, int size, Authentication authentication) {
        String currentUserId = authentication.getName();
        PageRequest conversationPage = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        List<Conversation> conversations = conversationRepository.findByParticipantsContainsOrderByUpdatedAtDesc(authentication.getName(), conversationPage);

        if(conversations.isEmpty())
            return Collections.emptyList();

        // Logic: For every conversation found, fetch the recent X messages
        return conversations.stream().map(conversation -> {
            Pageable messagePage = PageRequest.of(0, 20, Sort.by("deliveryTimestamp").descending());
            Slice<Message> messageSlice = messageRepository.findByConversationIdOrderByTimestampDesc(
                    conversation.getId(), messagePage
            );

            return buildHistoryDto(conversation, messageSlice.getContent(), currentUserId, messageSlice.isLast(), messageSlice.getNumber());
        }).toList();
    }



    @PreAuthorize("authenticated()")
    public List<MessageHistoryDto> syncMessages(Long sinceTimestamp, Authentication authentication) {
        String currentUserId = authentication.getName();
        Instant sinceInstant = Instant.ofEpochMilli(sinceTimestamp);

        List<Conversation> conversations = conversationRepository.findByParticipantsContainsAndUpdatedAtAfter(currentUserId, sinceInstant);

        if (conversations.isEmpty())
            return Collections.emptyList();

        return conversations.stream().map(conversation -> {
            List<Message> messages = messageRepository.findByConversationIdAndTimestampIsAfterOrderByTimestampDesc(
                    conversation.getId(), sinceInstant
            );

            return buildHistoryDto(conversation, messages, currentUserId, true, 0);
        }).toList();
    }


    // PRIVATE HELPER METHODS


    private MessageHistoryDto buildHistoryDto(Conversation conversation, List<Message> messages, String currentUserId, boolean isLast, int pageNumber) {
        Instant readCursor = conversation.getReadCursors().get(currentUserId); //Get readTimestamp of last message this user sent and was read.
        Instant deliveredCursor = conversation.getDeliveryCursors().get(currentUserId); //Get last readTimestamp of message this user sent and was delivered

        List<MessageDto> messageDtos = messages.stream()
                .map(msg -> {
                    MessageStatus status = determineMessageStatus(msg, currentUserId, readCursor, deliveredCursor);
                    return MessageMapper.mapToDto(msg, status);
                })
                .toList();

        return new MessageHistoryDto(
                conversation.getId(),
                messageDtos,
                deliveredCursor,
                readCursor,
                pageNumber,
                0,
                isLast
        );
    }


    private String getPartnerId(Conversation conversation, String currentUserId) {
        return conversation.getParticipants().stream()
                .filter(id -> !id.equals(currentUserId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Conversation " + conversation.getId() + " has invalid participants."));
    }


    //Determines if a given message is SENT, DELIVERED and/or READ
    private MessageStatus determineMessageStatus(Message msg, String currentUserId, Instant readCursor, Instant deliveredCursor) {
        //If i sent the message, for me, its already read.
        if (!msg.getSenderId().equals(currentUserId)) {
            return MessageStatus.READ;
        }

        //i sent this message. Has my partner read it?
        //Check if message time is BEFORE my partner's read cursor.
        if (readCursor != null && !msg.getTimestamp().isAfter(readCursor)) {
            return MessageStatus.READ;
        }

        //i sent this message. has my partner received it?
        if (deliveredCursor != null && !msg.getTimestamp().isAfter(deliveredCursor)) {
            return MessageStatus.DELIVERED;
        }

        return MessageStatus.SENT;
    }


}
