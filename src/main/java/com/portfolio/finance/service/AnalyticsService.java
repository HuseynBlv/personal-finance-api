package com.portfolio.finance.service;

import com.portfolio.finance.entity.Account;
import com.portfolio.finance.entity.Transaction;
import com.portfolio.finance.entity.TransactionType;
import com.portfolio.finance.exception.BadRequestException;
import com.portfolio.finance.exception.ResourceNotFoundException;
import com.portfolio.finance.repository.AccountRepository;
import com.portfolio.finance.repository.UserRepository;
import com.portfolio.finance.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public BigDecimal calculateMonthlySpending(Long userId, YearMonth month) {
        if (month == null) {
            throw new BadRequestException("Month is required");
        }

        Set<Long> accountIds = getUserAccountIds(userId);
        if (accountIds.isEmpty()) {
            return BigDecimal.ZERO;
        }

        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        return transactionRepository.findTransactionsByDateBetween(startDate, endDate).stream()
                .filter(transaction -> accountIds.contains(transaction.getAccountId()))
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public Map<String, BigDecimal> calculateSpendingByCategory(Long userId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("startDate and endDate are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("startDate must be before or equal to endDate");
        }

        Set<Long> accountIds = getUserAccountIds(userId);
        if (accountIds.isEmpty()) {
            return Map.of();
        }

        List<Transaction> transactions = transactionRepository.findTransactionsByDateBetween(startDate, endDate);

        return transactions.stream()
                .filter(transaction -> accountIds.contains(transaction.getAccountId()))
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));
    }

    private Set<Long> getUserAccountIds(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return accountRepository.findByUserId(userId).stream()
                .map(Account::getId)
                .collect(Collectors.toSet());
    }
}
