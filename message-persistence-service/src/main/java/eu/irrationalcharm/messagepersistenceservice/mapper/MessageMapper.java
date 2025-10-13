package eu.irrationalcharm.messagepersistenceservice.mapper;

import eu.irrationalcharm.events.MessageEvent;
import eu.irrationalcharm.messagepersistenceservice.model.Message;

public final class MessageMapper {

    public static Message mapToMessage(MessageEvent messageEvent) {
        var message = new Message();
        message.setConversationId(messageEvent.conversationId());
        message.setSenderId(messageEvent.senderId());
        message.setContent(messageEvent.content());
        message.setTimestamp(messageEvent.timestamp());
        message.setTextType(messageEvent.textType());

        return message;
    }
}
