package com.sheshapay.sheshapay.repo;

import com.sheshapay.sheshapay.model.Card;
import com.sheshapay.sheshapay.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
