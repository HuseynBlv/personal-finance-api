package com.portfolio.finance.dto;

import com.portfolio.finance.entity.TransactionType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateTransactionRequest {

    @NotNull
    @Positive
    private Long accountId;

    @NotNull
    @Positive
    @Digits(integer = 19, fraction = 2)
    private BigDecimal amount;

    @NotBlank
    @Size(max = 100)
    private String category;

    @NotNull
    private TransactionType type;

    @Size(max = 500)
    private String description;

    @NotNull
    @PastOrPresent
    private LocalDate date;
}
