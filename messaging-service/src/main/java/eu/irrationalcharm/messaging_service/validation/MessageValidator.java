package eu.irrationalcharm.messaging_service.validation;

import eu.irrationalcharm.messaging_service.dto.ChatMessageDto;
import eu.irrationalcharm.messaging_service.service.InternalUserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Validates if the user is allowed to send message to recipient
 */
@Component
@RequiredArgsConstructor
public class MessageValidator implements ConstraintValidator<MessageValid, ChatMessageDto> {

    private final InternalUserService internalUserService;

    @Override
    public void initialize(MessageValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(ChatMessageDto value, ConstraintValidatorContext context) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        context.disableDefaultConstraintViolation();

        //Sender id same as authenticated user?
        if (!value.userId().equals(auth.getName())) {
            context.buildConstraintViolationWithTemplate("Sender's user ID doest not match the authenticated user.")
                    .addPropertyNode("internalId")
                    .addConstraintViolation();
            return false;
        }

        //Sending message to himself?
        if (value.userId().equals(value.recipientId())) {
            context.buildConstraintViolationWithTemplate("Cannot send a message to yourself")
                    .addPropertyNode("recipientId")
                    .addConstraintViolation();
            return false;
        }

        //Sender and recipients are friends?
        var senderSocialGraph = internalUserService.getAuthenticatedUserSocialGraph(auth.getName());
        Set<String> senderFriends = senderSocialGraph.friendsInternalId();
        boolean areFriends = senderFriends.stream()
                .anyMatch(userId -> userId.equals(value.recipientId()));

        if(!areFriends) {
            context.buildConstraintViolationWithTemplate("You can only send messages to users on your friends list.")
                    .addPropertyNode("recipientId")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
