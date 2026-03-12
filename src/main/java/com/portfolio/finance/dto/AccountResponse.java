package com.portfolio.finance.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AccountResponse {
    Long id;
    String name;
    BigDecimal balance;
    String currency;
    Long userId;
    Instant createdAt;
}
