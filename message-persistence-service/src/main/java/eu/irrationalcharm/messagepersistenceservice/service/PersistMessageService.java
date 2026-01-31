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


    public void updateDeliveryStatus(MsgDeliveredEvent msgDeliveredEvent) {
        Conversation conversation = conversationRepository.findById(msgDeliveredEvent.conversationId())
                .orElseThrow();

        Instant lastDeliveredMessage = conversation.getLastDeliveredTimestamps().get(msgDeliveredEvent.recipientId());

        if(lastDeliveredMessage != null && lastDeliveredMessage.isAfter(msgDeliveredEvent.timestamp()))
            return;

        conversation.getLastDeliveredTimestamps().put(msgDeliveredEvent.recipientId(), msgDeliveredEvent.timestamp());
        conversationRepository.save(conversation);
    }


    public void updateReadStatus(MsgReadEvent msgReadEvent) {
        Conversation conversation = conversationRepository.findById(msgReadEvent.conversationId())
                .orElseThrow();

        Instant lastReadMessage = conversation.getLastDeliveredTimestamps().get(msgReadEvent.recipientId());

        if(lastReadMessage != null && lastReadMessage.isAfter(msgReadEvent.timestamp()))
            return;

        conversation.getLastReadTimestamps().put(msgReadEvent.recipientId(), msgReadEvent.timestamp());

        conversationRepository.save(conversation);
    }
}
