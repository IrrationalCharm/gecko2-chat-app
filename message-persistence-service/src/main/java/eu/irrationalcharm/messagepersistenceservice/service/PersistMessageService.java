package eu.irrationalcharm.messagepersistenceservice.service;


import eu.irrationalcharm.events.chat.MessageEvent;
import eu.irrationalcharm.events.chat.MsgDeliveredEvent;
import eu.irrationalcharm.events.chat.MsgReadEvent;
import eu.irrationalcharm.messagepersistenceservice.mapper.MessageEventMapper;
import eu.irrationalcharm.messagepersistenceservice.model.Conversation;
import eu.irrationalcharm.messagepersistenceservice.model.LastMessage;
import eu.irrationalcharm.messagepersistenceservice.model.Message;
import eu.irrationalcharm.messagepersistenceservice.repository.ConversationRepository;
import eu.irrationalcharm.messagepersistenceservice.repository.MessageRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

import static eu.irrationalcharm.messagepersistenceservice.utils.ConversationUtils.generateConversationId;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersistMessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    public void persistMessage(MessageEvent messageEvent) {
        Message message = MessageEventMapper.mapToMessage(messageEvent);

        Conversation conversation = conversationRepository.findById(messageEvent.conversationId()).orElseGet(() -> {
            log.debug("Conversation {} does not exist yet. Creating a new conversation document.", messageEvent.conversationId());

            var newConversation = new Conversation();
            String conversationId = generateConversationId(messageEvent.senderId(), messageEvent.recipientId());
            newConversation.setId(conversationId);
            newConversation.setParticipants( Set.of(messageEvent.senderId(), messageEvent.recipientId()) );

            return newConversation;
        });

        conversation.setLastMessage(new LastMessage(
                                        messageEvent.clientMsgId(),
                                        messageEvent.senderId(),
                                        messageEvent.content(),
                                        messageEvent.timestamp(),
                                        messageEvent.textType()));

        conversationRepository.save(conversation);

        messageRepository.insert(message);

        log.debug("Successfully persisted message {} for conversation {}", messageEvent.clientMsgId(), messageEvent.conversationId());
    }


    /**
     * ReceiverId refers to the user who got the message on their phone and confirmed delivery.
     */
    public void updateDeliveryStatus(MsgDeliveredEvent event) {
        Conversation conversation = conversationRepository.findById(event.conversationId())
                .orElseThrow(() -> {
                    log.warn("Cannot update delivery status. Conversation {} not found in database.", event.conversationId());
                    return new RuntimeException("Conversation not found");
                });

        String receiverId = event.receiverId();
        Instant deliveredCursor = conversation.getDeliveryCursors().get(receiverId);

        if(deliveredCursor != null && deliveredCursor.isAfter(event.deliveryTimestamp())) {
            log.debug("Skipped delivery cursor update for conversation {}. Existing cursor is newer.", event.conversationId());
            return;
        }


        conversation.getDeliveryCursors().put(receiverId, event.deliveryTimestamp());
        conversationRepository.save(conversation);
        log.debug("Updated delivery cursor for user {} in conversation {}", receiverId, event.conversationId());
    }


    /**
     * ReaderId refers to the user who read the message
     */
    public void updateReadStatus(MsgReadEvent event) {
        Conversation conversation = conversationRepository.findById(event.conversationId())
                .orElseThrow(() -> {
                    log.warn("Cannot update read status. Conversation {} not found in database.", event.conversationId());
                    return new RuntimeException("Conversation not found");
                });

        String readerId = event.readerId();
        Instant readCursor = conversation.getReadCursors().get(readerId);

        if(readCursor != null && readCursor.isAfter(event.readTimestamp())) {
            log.debug("Skipped read cursor update for conversation {}. Existing cursor is newer.", event.conversationId());
            return;
        }

        conversation.getReadCursors().put(readerId, event.readTimestamp());
        conversationRepository.save(conversation);
        log.debug("Updated read cursor for user {} in conversation {}", readerId, event.conversationId());
    }
}
