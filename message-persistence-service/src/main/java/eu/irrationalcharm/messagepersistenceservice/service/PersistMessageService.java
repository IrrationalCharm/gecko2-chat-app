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

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

import static eu.irrationalcharm.messagepersistenceservice.utils.ConversationUtils.generateConversationId;

@Service
@RequiredArgsConstructor
public class PersistMessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    public void persistMessage(MessageEvent messageEvent) {
        Message message = MessageEventMapper.mapToMessage(messageEvent);

        Conversation conversation = conversationRepository.findById(messageEvent.conversationId()).orElseGet(() -> {
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
    }


    /**
     * ReceiverId refers to the user who got the message on their phone and confirmed delivery.
     */
    public void updateDeliveryStatus(MsgDeliveredEvent event) {
        Conversation conversation = conversationRepository.findById(event.conversationId())
                .orElseThrow();

        String receiverId = event.receiverId();
        Instant deliveredCursor = conversation.getDeliveryCursors().get(receiverId);

        if(deliveredCursor != null && deliveredCursor.isAfter(event.deliveryTimestamp()))
            return;

        conversation.getDeliveryCursors().put(receiverId, event.deliveryTimestamp());
        conversationRepository.save(conversation);
    }


    /**
     * ReaderId refers to the user who read the message
     */
    public void updateReadStatus(MsgReadEvent event) {
        Conversation conversation = conversationRepository.findById(event.conversationId())
                .orElseThrow();

        String readerId = event.readerId();
        Instant readCursor = conversation.getReadCursors().get(readerId);

        if(readCursor != null && readCursor.isAfter(event.readTimestamp()))
            return;

        conversation.getReadCursors().put(readerId, event.readTimestamp());

        conversationRepository.save(conversation);
    }
}
