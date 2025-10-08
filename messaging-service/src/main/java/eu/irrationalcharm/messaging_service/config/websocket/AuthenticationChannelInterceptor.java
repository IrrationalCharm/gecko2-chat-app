package eu.irrationalcharm.messaging_service.config.websocket;

import eu.irrationalcharm.messaging_service.client.dto.UserSocialGraphDto;
import eu.irrationalcharm.messaging_service.security.CustomWebSocketAuthToken;
import eu.irrationalcharm.messaging_service.security.JwtAuthenticationConverter;
import eu.irrationalcharm.messaging_service.service.InternalUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import org.springframework.stereotype.Component;

import java.util.Objects;


@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter authenticationConverter;
    private final InternalUserService internalUserService;
    private final WebSocketSessionRegistry webSocketSessionRegistry;

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (Objects.requireNonNull(accessor.getCommand()) == StompCommand.CONNECT) {
            String authHeader = accessor.getFirstNativeHeader(AUTHORIZATION);
            CustomWebSocketAuthToken jwtAuthToken = getValidAuthenticationOrThrow(authHeader);

            SecurityContextHolder.getContext().setAuthentication(jwtAuthToken);
            var userSocialGraphDto = internalUserService.getAuthenticatedUserSocialGraph(jwtAuthToken.getName());

            validateUserOrThrow(userSocialGraphDto);


            jwtAuthToken.setAuthenticated(true);
            accessor.setUser(jwtAuthToken);
        }


        //Checks if websocket is already subscribed to destination
        if (Objects.requireNonNull(accessor.getCommand()) == StompCommand.SUBSCRIBE) {
            Authentication auth = (Authentication) accessor.getUser();

            if(auth instanceof CustomWebSocketAuthToken customAuth) {
                String sessionId = accessor.getSessionId();
                String destination = accessor.getDestination();

                assert destination != null;
                if (webSocketSessionRegistry.isSubscribed(customAuth.getName(), destination)) {
                    log.warn("User with session {} is already subscribed to {}. Ignoring request.", sessionId, destination);
                    return null;
                }
            }
        }


        return message;
    }


    private void validateUserOrThrow(UserSocialGraphDto userSocialGraphDto) {
        if(!userSocialGraphDto.isOnBoarded()) throw new RuntimeException("User cannot connect, hasn't completed OnBoarding!");
    }


    private CustomWebSocketAuthToken getValidAuthenticationOrThrow(String authHeader) {
        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            throw new BadCredentialsException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(BEARER.length());
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return authenticationConverter.convert(jwt);

        } catch (JwtException jwtException) {
            log.warn("Invalid JWT token: {}", jwtException.getMessage());
            throw new BadCredentialsException("Invalid JWT token");
        }
    }
}
