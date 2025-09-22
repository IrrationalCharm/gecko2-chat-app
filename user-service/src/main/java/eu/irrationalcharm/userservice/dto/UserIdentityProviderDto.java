package eu.irrationalcharm.userservice.dto;

import eu.irrationalcharm.userservice.enums.IdentityProviderType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link eu.irrationalcharm.userservice.entity.UserIdentityProviderEntity}
 */
@Builder
public record UserIdentityProviderDto(UUID userId,
                                      @NotNull @Size(max = 50) IdentityProviderType provider,
                                      @NotNull String providerUserId,
                                      @NotNull String email) implements Serializable {
}