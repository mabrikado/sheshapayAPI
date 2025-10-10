package com.sheshapay.sheshapay.repo;

import com.sheshapay.sheshapay.model.Account;
import com.sheshapay.sheshapay.model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByToAccount(Account account);

    List<Transaction> findByFromAccount(Account account);

    List<Transaction> findByFromAccountAndTimestampBetween(Account account, LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByToAccountAndTimestampBetween(Account account, LocalDateTime startDate, LocalDateTime endDate);

    // Add pageable methods for pagination:
    Page<Transaction> findByToAccount(Account account, Pageable pageable);

    Page<Transaction> findByFromAccount(Account account, Pageable pageable);
}
