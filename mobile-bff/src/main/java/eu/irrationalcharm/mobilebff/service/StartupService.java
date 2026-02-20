package eu.irrationalcharm.mobilebff.service;

import eu.irrationalcharm.dto.user_service.FriendRequestDto;
import eu.irrationalcharm.dto.persistence_service.MessageHistoryDto;
import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.dto.user_service.UserDto;
import eu.irrationalcharm.mobilebff.client.PersistenceServiceClient;
import eu.irrationalcharm.mobilebff.client.UserServiceClient;
import eu.irrationalcharm.mobilebff.dto.StartupDataDto;
import eu.irrationalcharm.mobilebff.wrapper.PersistenceClientWrapper;
import eu.irrationalcharm.mobilebff.wrapper.UserServiceClientWrapper;
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

    private final UserServiceClientWrapper userServiceClient;
    private final PersistenceClientWrapper persistenceServiceClient;

    public StartupService(@Qualifier("applicationTaskExecutor") AsyncTaskExecutor taskExecutor,
                                                                UserServiceClientWrapper userServiceClient,
                                                                PersistenceClientWrapper persistenceServiceClient) {
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
        return userServiceClient.pendingFriendRequests();
    }

    private UserDto fetchUserData() {
        return userServiceClient.fetchMe();
    }

    private List<MessageHistoryDto> getSyncConversation(Long sinceTimestamp) {
        long since = sinceTimestamp != null ? sinceTimestamp : 0;
        return persistenceServiceClient.getSyncConversation(since);
    }

    private Set<PublicUserResponseDto> fetchUserFriends() {
        return userServiceClient.getFriends();
    }


}
