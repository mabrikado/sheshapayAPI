package com.sheshapay.sheshapay.repo;

import com.sheshapay.sheshapay.model.Account;
import com.sheshapay.sheshapay.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByToAccount(Account account);
    List<Transaction> findByFromAccount(Account account);
}
