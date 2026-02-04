package eu.irrationalcharm.messaging_service.service.orchestrator;

import eu.irrationalcharm.events.FriendRequestEvent;
import eu.irrationalcharm.events.NotificationEvent;
import eu.irrationalcharm.messaging_service.config.websocket.WebSocketSessionRegistry;
import eu.irrationalcharm.messaging_service.dto.response.*;
import eu.irrationalcharm.messaging_service.enums.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceOrchestrator {

    private final WebSocketSessionRegistry sessionRegistry;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final JsonMapper objectMapper;


    public void receivedNotification(NotificationEvent notificationEvent) {

        switch(notificationEvent.type()) {
            case FRIEND_REQUEST_RECEIVED -> {
                //Internally payload is a LinkedHashMap since jackson didn't know to what to convert this into
                var event = objectMapper.convertValue(
                        notificationEvent.payload(),
                        FriendRequestEvent.class
                );
                var friendRequestReceivedDto = new FriendRequestPayload(
                        MessageType.FRIEND_REQUEST_SERVER,
                        event.requestId(),
                        event.initiatorId(),
                        event.initiatorUsername(),
                        event.initiatorDisplayName(),
                        event.initiatorProfileImageUrl(),
                        event.createdAt()
                );

                //Is the user connected to this messaging-service
                boolean isConnected = sessionRegistry.isRegistered(notificationEvent.requestReceiverId());

                if (isConnected) {
                    internalSendPrivateMessage(notificationEvent.requestReceiverId(), friendRequestReceivedDto);
                }

                //TODO fan out to redis to find websocket
            }
            case FRIEND_REQUEST_ACCEPTED -> {

            }
            case FRIEND_REMOVED, PROFILE_UPDATED, USER_BLOCKED -> log.warn("Notifications not implemented yet");

        }

    }

    private void internalSendPrivateMessage(String recipientId, ServerMessage message) {
        log.debug("Sending internal message to: {}", recipientId);

        switch(message) {
            case FriendRequestPayload friendRequestPayload -> simpMessagingTemplate.convertAndSendToUser(recipientId,"/private", friendRequestPayload);

            case ChatMessagePayload _, MessageSentPayload _, MessageDeliveredPayload _ -> log.error("Not a valid notification message");
            case MessageReadPayload messageReadPayload -> {
            }
        }
    }
}
