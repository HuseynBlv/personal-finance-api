package com.portfolio.finance.service;

import com.portfolio.finance.dto.CreateTransactionRequest;
import com.portfolio.finance.dto.TransactionResponse;
import com.portfolio.finance.entity.Account;
import com.portfolio.finance.entity.Transaction;
import com.portfolio.finance.entity.TransactionType;
import com.portfolio.finance.exception.BadRequestException;
import com.portfolio.finance.exception.InsufficientBalanceException;
import com.portfolio.finance.exception.ResourceNotFoundException;
import com.portfolio.finance.repository.AccountRepository;
import com.portfolio.finance.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + request.getAccountId()));

        if (request.getType() == TransactionType.EXPENSE && account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for this expense transaction");
        }

        adjustAccountBalance(account, request.getType(), request.getAmount());

        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(request.getAmount())
                .category(request.getCategory())
                .type(request.getType())
                .description(request.getDescription())
                .date(request.getDate())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        accountRepository.save(account);

        return toResponse(savedTransaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByAccount(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account not found with id: " + accountId);
        }

        return transactionRepository.findByAccountId(accountId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> filterTransactions(
            Long accountId,
            String category,
            LocalDate startDate,
            LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("startDate must be before or equal to endDate");
        }

        if (accountId != null && !accountRepository.existsById(accountId)) {
            throw new ResourceNotFoundException("Account not found with id: " + accountId);
        }

        List<Transaction> transactions = getBaseTransactions(category, startDate, endDate);

        return transactions.stream()
                .filter(transaction -> accountId == null || accountId.equals(transaction.getAccountId()))
                .filter(transaction -> category == null || category.isBlank()
                        || category.equalsIgnoreCase(transaction.getCategory()))
                .map(this::toResponse)
                .toList();
    }

    private List<Transaction> getBaseTransactions(String category, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return transactionRepository.findTransactionsByDateBetween(startDate, endDate);
        }
        if (category != null && !category.isBlank()) {
            return transactionRepository.findByCategory(category);
        }
        return transactionRepository.findAll();
    }

    private void adjustAccountBalance(Account account, TransactionType type, BigDecimal amount) {
        BigDecimal updatedBalance = type == TransactionType.INCOME
                ? account.getBalance().add(amount)
                : account.getBalance().subtract(amount);
        account.setBalance(updatedBalance);
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccountId())
                .amount(transaction.getAmount())
                .category(transaction.getCategory())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .date(transaction.getDate())
                .build();
    }
}
