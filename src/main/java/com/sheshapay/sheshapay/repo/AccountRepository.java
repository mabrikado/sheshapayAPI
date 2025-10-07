package com.sheshapay.sheshapay.repo;

import com.sheshapay.sheshapay.model.Account;
import com.sheshapay.sheshapay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByAccountNumber(String accountNumber);
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findByUser(User user);
}
