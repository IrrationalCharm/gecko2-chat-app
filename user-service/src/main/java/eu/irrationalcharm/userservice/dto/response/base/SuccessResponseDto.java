package eu.irrationalcharm.userservice.dto.response.base;


import lombok.Builder;
import lombok.With;

import java.time.Instant;

@Builder
@With //It generates a method that "modifies" an immutable object by creating and returning a new, modified instance.
public record SuccessResponseDto<T>(
        String code,    // short summary like "OK" or "User created"
        int status,      // numeric HTTP code
        String detail,   // optional human-readable explanation
        T data,          // payload
        String instance, // request path
        Instant timestamp // optional extra metadata
) {}