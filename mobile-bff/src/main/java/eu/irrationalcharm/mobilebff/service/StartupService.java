package eu.irrationalcharm.mobilebff.service;

import eu.irrationalcharm.dto.user_service.FriendRequestDto;
import eu.irrationalcharm.dto.persistence_service.MessageHistoryDto;
import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.dto.user_service.UserDto;
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
import java.util.concurrent.TimeUnit;

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
        var profileFuture = CompletableFuture.supplyAsync(this::fetchUserData, taskExecutor)
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.warn("Profile fetch timed out or failed: {}", ex.getMessage());
                    return null;
                });

        var friendsFuture = CompletableFuture.supplyAsync(this::fetchUserFriends, taskExecutor)
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.warn("Friends fetch timed out or failed: {}", ex.getMessage());
                    return Collections.emptySet();
                });

        var lastMessages = CompletableFuture.supplyAsync(() -> getSyncConversation(sinceTimestamp), taskExecutor)
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.warn("Messages fetch timed out or failed: {}", ex.getMessage());
                    return Collections.emptyList();
                });

        var friendRequests = CompletableFuture.supplyAsync(this::fetchPendingFriendRequests, taskExecutor)
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.warn("Friend requests fetch timed out or failed: {}", ex.getMessage());
                    return Collections.emptyList();
                });

        CompletableFuture.allOf(profileFuture, friendsFuture, lastMessages, friendRequests).join();

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
