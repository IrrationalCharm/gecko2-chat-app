package eu.irrationalcharm.messagepersistenceservice.mapper;

import eu.irrationalcharm.events.chat.MessageEvent;
import eu.irrationalcharm.messagepersistenceservice.model.Message;

public final class MessageEventMapper {

    public static Message mapToMessage(MessageEvent messageEvent) {
        var message = new Message();
        message.setClientMsgId(messageEvent.clientMsgId());
        message.setConversationId(messageEvent.conversationId());
        message.setSenderId(messageEvent.senderId());
        message.setContent(messageEvent.content());
        message.setTimestamp(messageEvent.timestamp());
        message.setTextType(messageEvent.textType());

        return message;
    }
}
