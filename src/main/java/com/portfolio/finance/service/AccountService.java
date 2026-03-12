package com.portfolio.finance.service;

import com.portfolio.finance.dto.AccountResponse;
import com.portfolio.finance.dto.CreateAccountRequest;
import com.portfolio.finance.entity.Account;
import com.portfolio.finance.entity.User;
import com.portfolio.finance.exception.BadRequestException;
import com.portfolio.finance.exception.ResourceNotFoundException;
import com.portfolio.finance.repository.AccountRepository;
import com.portfolio.finance.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Account account = Account.builder()
                .name(request.getName())
                .balance(request.getBalance())
                .currency(request.getCurrency())
                .user(user)
                .build();

        return toResponse(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getUserAccounts(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return accountRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccounts(Long userId) {
        if (userId != null) {
            return getUserAccounts(userId);
        }

        return accountRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        return toResponse(account);
    }

    @Transactional
    public AccountResponse updateAccountBalance(Long accountId, BigDecimal newBalance) {
        if (newBalance == null || newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Balance must be greater than or equal to zero");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        account.setBalance(newBalance);
        return toResponse(accountRepository.save(account));
    }

    private AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .userId(account.getUserId())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
