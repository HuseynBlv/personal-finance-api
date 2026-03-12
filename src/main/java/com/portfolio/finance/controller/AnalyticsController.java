package com.portfolio.finance.controller;

import com.portfolio.finance.dto.CategorySpendingResponse;
import com.portfolio.finance.dto.MonthlySpendingResponse;
import com.portfolio.finance.service.AnalyticsService;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/monthly")
    public ResponseEntity<MonthlySpendingResponse> getMonthlySpending(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        MonthlySpendingResponse response = MonthlySpendingResponse.builder()
                .userId(userId)
                .month(month)
                .totalSpending(analyticsService.calculateMonthlySpending(userId, month))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<CategorySpendingResponse> getSpendingByCategory(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        CategorySpendingResponse response = CategorySpendingResponse.builder()
                .userId(userId)
                .startDate(startDate)
                .endDate(endDate)
                .spendingByCategory(analyticsService.calculateSpendingByCategory(userId, startDate, endDate))
                .build();
        return ResponseEntity.ok(response);
    }
}
