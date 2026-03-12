package com.portfolio.finance.repository;

import com.portfolio.finance.entity.Transaction;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountId(Long accountId);

    List<Transaction> findByCategory(String category);

    List<Transaction> findTransactionsByDateBetween(LocalDate startDate, LocalDate endDate);
}
