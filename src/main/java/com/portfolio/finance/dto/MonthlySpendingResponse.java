package com.portfolio.finance.dto;

import java.math.BigDecimal;
import java.time.YearMonth;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MonthlySpendingResponse {
    Long userId;
    YearMonth month;
    BigDecimal totalSpending;
}
