package com.portfolio.finance.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserResponse {
    Long id;
    String email;
    Instant createdAt;
}
