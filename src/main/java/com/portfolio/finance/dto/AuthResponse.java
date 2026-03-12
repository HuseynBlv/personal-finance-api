package com.portfolio.finance.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthResponse {
    String accessToken;
    String tokenType;
    Long expiresIn;
    Long userId;
    String email;
}
