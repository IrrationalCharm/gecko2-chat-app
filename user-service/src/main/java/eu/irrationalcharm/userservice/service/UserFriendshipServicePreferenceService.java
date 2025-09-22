package eu.irrationalcharm.userservice.service;

import eu.irrationalcharm.userservice.repository.UserFriendshipPreferenceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserFriendshipServicePreferenceService {

    private final UserFriendshipPreferenceRepository userFriendshipPreferenceRepository;

    public boolean isBlocking(UUID potentialBlockerId, UUID potentialBlockedId) {
        var potencialBlockerFriendPreferenceOptional = userFriendshipPreferenceRepository.findByUserIdAndFriendId(potentialBlockerId, potentialBlockedId);

        if (potencialBlockerFriendPreferenceOptional.isPresent())
            return potencialBlockerFriendPreferenceOptional.get().getIsBlocked();

        return false;
    }
    
}
