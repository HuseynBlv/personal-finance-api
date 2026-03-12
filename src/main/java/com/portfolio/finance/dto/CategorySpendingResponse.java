package com.portfolio.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CategorySpendingResponse {
    Long userId;
    LocalDate startDate;
    LocalDate endDate;
    Map<String, BigDecimal> spendingByCategory;
}
