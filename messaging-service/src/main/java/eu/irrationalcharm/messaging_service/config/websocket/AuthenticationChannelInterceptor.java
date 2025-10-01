package eu.irrationalcharm.messaging_service.config.websocket;

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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter authenticationConverter;
    private final InternalUserService internalUserService;

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if(StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader(AUTHORIZATION);

            JwtAuthenticationToken jwtAuthToken = getAuthenticationOrThrow(authHeader);
            accessor.setUser(jwtAuthToken);

            validateUserOrThrow(jwtAuthToken);
        }
        return message;
    }


    private void validateUserOrThrow(JwtAuthenticationToken jwtAuthToken) {
        var userSocialGraphDto = internalUserService.getUserSocialGraphDto(jwtAuthToken.getName());
        System.out.println(userSocialGraphDto);

        if(!userSocialGraphDto.isOnBoarded()) throw new RuntimeException("User cannot connect, hasn't completed OnBoarding");
    }


    private JwtAuthenticationToken getAuthenticationOrThrow(String authHeader) {
        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            throw new BadCredentialsException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(BEARER.length());
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return (JwtAuthenticationToken) authenticationConverter.convert(jwt);

        } catch (JwtException jwtException) {
            log.warn("Invalid JWT token: {}", jwtException.getMessage());
            throw new BadCredentialsException("Invalid JWT token");
        }
    }
}
