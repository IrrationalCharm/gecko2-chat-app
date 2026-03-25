package eu.irrationalcharm.userservice.application.port.in;

import eu.irrationalcharm.userservice.dto.response.FriendPreferenceDto;
import eu.irrationalcharm.userservice.dto.response.PatchFriendPreferenceDto;

import java.util.UUID;

public interface ManageFriendPreferenceUseCase {

    FriendPreferenceDto getFriendPreference(UUID principalId, String friendUsername);

    PatchFriendPreferenceDto updateFriendPreference(UUID principalId, String friendUsername, PatchFriendPreferenceDto dto);
}
