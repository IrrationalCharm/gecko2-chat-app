package eu.irrationalcharm.messaging_service.config.websocket;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TraceContextChannelInterceptor implements ChannelInterceptor {

    private final ObservationRegistry observationRegistry;
    private final ThreadLocal<Observation.Scope> scopeThreadLocal = new ThreadLocal<>();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // Starts a new trace/span for the incoming STOMP message
        Observation observation = Observation.start("stomp.message", observationRegistry);
        scopeThreadLocal.set(observation.openScope());
        return message;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        Observation.Scope scope = scopeThreadLocal.get();
        if (scope != null) {
            scope.close();
            scopeThreadLocal.remove();

            Observation observation = scope.getCurrentObservation();
            if (ex != null) {
                observation.error(ex);
            }
            observation.stop();
        }
    }
}
