package com.portfolio.finance.dto;

import com.portfolio.finance.entity.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TransactionResponse {
    Long id;
    Long accountId;
    BigDecimal amount;
    String category;
    TransactionType type;
    String description;
    LocalDate date;
}
