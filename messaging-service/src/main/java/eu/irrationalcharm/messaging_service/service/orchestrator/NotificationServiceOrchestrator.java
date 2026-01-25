package eu.irrationalcharm.messaging_service.service.orchestrator;

import eu.irrationalcharm.events.FriendRequestEvent;
import eu.irrationalcharm.events.NotificationEvent;
import eu.irrationalcharm.messaging_service.config.websocket.WebSocketSessionRegistry;
import eu.irrationalcharm.messaging_service.dto.ChatMessageDto;
import eu.irrationalcharm.messaging_service.dto.FriendRequestReceivedDto;
import eu.irrationalcharm.messaging_service.dto.MessageReceivedDto;
import eu.irrationalcharm.messaging_service.dto.PrivateMessage;
import eu.irrationalcharm.messaging_service.enums.PrivateMessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceOrchestrator {

    private final WebSocketSessionRegistry sessionRegistry;
    private final SimpMessagingTemplate simpMessagingTemplate;


    public void receivedNotification(NotificationEvent notificationEvent) {

        switch(notificationEvent.type()) {
            case FRIEND_REQUEST_RECEIVED -> {
                var dto = (FriendRequestEvent) notificationEvent.payload();
                var friendRequestReceivedDto = new FriendRequestReceivedDto(
                        PrivateMessageType.FRIEND_REQUEST_RECEIVED,
                        dto.initiatorId(),
                        dto.initiatorUsername(),
                        dto.initiatorProfileImageUrl(),
                        Instant.ofEpochMilli(dto.createdAt())
                );

                //Is the user connected to this messaging-service
                boolean isConnected = sessionRegistry.isRegistered(notificationEvent.requestReceiverId());

                if (isConnected) {
                    internalSendPrivateMessage(notificationEvent.requestReceiverId(), friendRequestReceivedDto);
                }
            }
            case FRIEND_REQUEST_ACCEPTED -> {

            }
            case FRIEND_REMOVED, PROFILE_UPDATED, USER_BLOCKED -> log.warn("Notifications not implemented yet");

        }

    }

    private void internalSendPrivateMessage(String recipientId, PrivateMessage message) {
        log.debug("Sending internal message to: {}", recipientId);

        switch(message) {
            case FriendRequestReceivedDto friendRequestReceivedDto -> simpMessagingTemplate.convertAndSendToUser(recipientId,"/private", friendRequestReceivedDto);

            case ChatMessageDto _, MessageReceivedDto _ -> log.error("Not a valid notification message");


        }
    }
}
