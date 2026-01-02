package eu.irrationalcharm.mobilebff.service;

import eu.irrationalcharm.dto.persistence_service.ConversationSummaryDto;
import eu.irrationalcharm.dto.user_service.PublicUserResponseDto;
import eu.irrationalcharm.dto.user_service.UserDto;
import eu.irrationalcharm.mobilebff.client.PersistenceServiceClient;
import eu.irrationalcharm.mobilebff.client.UserServiceClient;
import eu.irrationalcharm.mobilebff.dto.StartupDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

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


    public StartupDataDto getStartupData() {
        var profileFuture = CompletableFuture.supplyAsync(this::fetchUserData, taskExecutor);
        var friendsFuture = CompletableFuture.supplyAsync(this::fetchUserFriends, taskExecutor);
        var lastMessages = CompletableFuture.supplyAsync(this::fetchLastMessages, taskExecutor);

        CompletableFuture.allOf(profileFuture, friendsFuture, lastMessages).join();
        log.info("Successfully fetched data on start up");
        return new StartupDataDto(
                profileFuture.join(),
                friendsFuture.join(),
                lastMessages.join()
        );
    }


    private UserDto fetchUserData() {
        try {
            return userServiceClient.fetchMe().getBody().data();

        } catch (Exception e) {
            log.error("Failed to fetch user profile from user-service", e);
            throw new RuntimeException(e);
        }
    }


    private List<ConversationSummaryDto> fetchLastMessages() {
        try {
            return persistenceServiceClient.getSummaryMessages().getBody().data();

        } catch (Exception e) {
            log.error("Failed to fetch last messages from message-persitence-service", e);
            throw new RuntimeException(e);
        }
    }


    private Set<PublicUserResponseDto> fetchUserFriends() {
        try {
            return userServiceClient.getFriends().getBody().data();

        } catch (Exception e) {
            log.error("Failed to fetch user friends list from user-service", e);
            throw new RuntimeException(e);
        }
    }


}
