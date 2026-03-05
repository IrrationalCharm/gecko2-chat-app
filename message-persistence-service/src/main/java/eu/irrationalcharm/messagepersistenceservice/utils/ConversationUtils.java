package eu.irrationalcharm.messagepersistenceservice.utils;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConversationUtils {

    public static String generateConversationId(String userA, String userB) {
        if(userA.compareTo(userB) > 0) {
            return String.format("%s:%s", userA, userB);
        } else
            return String.format("%s:%s", userB, userA);

    }
}
