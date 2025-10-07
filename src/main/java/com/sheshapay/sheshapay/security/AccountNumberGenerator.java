package com.sheshapay.sheshapay.security;

import java.security.SecureRandom;

import com.sheshapay.sheshapay.repo.AccountRepository;
import org.springframework.stereotype.Component;

@Component
public class AccountNumberGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final String PREFIX = "27"; // SheshaPay code

    private final AccountRepository accountRepository; // inject repo

    public AccountNumberGenerator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Generate unique account number (10–12 digits).
     */
    public String generateUniqueAccountNumber(Long userId) {
        String accountNo;
        do {
            accountNo = generateAccountNumber(userId);
        } while (accountRepository.existsByAccountNumber(accountNo));
        return accountNo;
    }

    private String generateAccountNumber(Long userId) {
        // Pad userId to 6 digits minimum
        String paddedId = String.format("%06d", userId);

        // Add 2–4 random digits (so total length is 10–12)
        int extraDigitsCount = random.nextInt(3) + 2; // 2, 3, or 4
        StringBuilder randomDigits = new StringBuilder();
        for (int i = 0; i < extraDigitsCount; i++) {
            randomDigits.append(random.nextInt(10));
        }

        return PREFIX + paddedId + randomDigits;
    }
}
