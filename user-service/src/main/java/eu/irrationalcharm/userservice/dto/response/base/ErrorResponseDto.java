package eu.irrationalcharm.userservice.dto.response.base;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ErrorResponseDto<T>(String type,
                                  String code,
                                  int status,
                                  String detail,
                                  T error,
                                  String instance,
                                  Instant timestamp) {
}
