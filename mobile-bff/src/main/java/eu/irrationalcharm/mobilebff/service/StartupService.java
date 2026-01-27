package eu.irrationalcharm.mobilebff.service;

import eu.irrationalcharm.dto.user_service.FriendRequestDto;
import eu.irrationalcharm.dto.persistence_service.MessageHistoryDto;
import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.dto.user_service.UserDto;
import eu.irrationalcharm.mobilebff.client.PersistenceServiceClient;
import eu.irrationalcharm.mobilebff.client.UserServiceClient;
import eu.irrationalcharm.mobilebff.dto.StartupDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class StartupService {

    //Spring managed executor (propagates Authentication & tracing into VT)
    private final AsyncTaskExecutor taskExecutor;

    private final UserServiceClient userServiceClient;
    private final PersistenceServiceClient persistenceServiceClient;

    public StartupService(@Qualifier("applicationTaskExecutor") AsyncTaskExecutor taskExecutor,
                                                                UserServiceClient userServiceClient,
                                                                PersistenceServiceClient persistenceServiceClient) {
        this.taskExecutor = taskExecutor;
        this.userServiceClient = userServiceClient;
        this.persistenceServiceClient = persistenceServiceClient;
    }


    public StartupDataDto getStartupData(Long sinceTimestamp) {
        var profileFuture = CompletableFuture.supplyAsync(this::fetchUserData, taskExecutor);
        var friendsFuture = CompletableFuture.supplyAsync(this::fetchUserFriends, taskExecutor);
        var lastMessages = CompletableFuture.supplyAsync( () -> getSyncConversation(sinceTimestamp), taskExecutor);
        var friendRequests = CompletableFuture.supplyAsync(this::fetchPendingFriendRequests, taskExecutor);

        CompletableFuture.allOf(profileFuture, friendsFuture, lastMessages).join();
        log.info("Successfully fetched data on start up");
        return new StartupDataDto(
                profileFuture.join(),
                friendsFuture.join(),
                lastMessages.join(),
                friendRequests.join()
        );
    }

    private List<FriendRequestDto> fetchPendingFriendRequests() {
        try {
            var response = userServiceClient.pendingFriendRequests().getBody().data();
            if (response != null)
                log.info("Successfully retrieved pending requests from user-service");

            return response;
        } catch (Exception e) {
            log.error("Failed to fetch pending friend requests from user-service", e);
            return Collections.emptyList();
        }
    }


    private UserDto fetchUserData() {
        try {
            var response = userServiceClient.fetchMe().getBody().data();
            if (response != null)
                log.info("Successfully retrieved User data from user-service");

            return response;
        } catch (Exception e) {
            log.error("Failed to fetch user profile from user-service", e);
            throw new RuntimeException(e);
        }
    }


    private List<MessageHistoryDto> getSyncConversation(Long sinceTimestamp) {
        long since = sinceTimestamp != null ? sinceTimestamp : 0;
        try {
            var response = persistenceServiceClient.getSyncConversation(since).getBody().data();
            if (response != null)
                log.info("Successfully retrieved last messages from message-persistence-service");

            return response;
        } catch (Exception e) {
            log.error("Failed to fetch last messages from message-persitence-service", e);
            return Collections.emptyList();
        }
    }


    private Set<PublicUserResponseDto> fetchUserFriends() {
        try {
            var response = userServiceClient.getFriends().getBody().data();
            if (response != null)
                log.info("Successfully retrieved users friends from user-service");

            return response;
        } catch (Exception e) {
            log.error("Failed to fetch user friends list from user-service", e);
            return Collections.emptySet();
        }
    }


}
