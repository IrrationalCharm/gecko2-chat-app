package eu.irrationalcharm.events.chat;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

// This annotation tells Jackson to include the class name in the JSON so consumers know which record to deserialize
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public sealed interface ChatEvent permits MessageEvent, MsgDeliveredEvent, MsgReadEvent{

    String conversationId();
}
