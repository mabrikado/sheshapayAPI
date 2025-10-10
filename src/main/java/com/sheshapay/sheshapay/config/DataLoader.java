package com.sheshapay.sheshapay.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheshapay.sheshapay.form.CardForm;
import com.sheshapay.sheshapay.form.RegisterForm;
import com.sheshapay.sheshapay.model.Account;
import com.sheshapay.sheshapay.service.AccountService;
import com.sheshapay.sheshapay.service.CardService;
import com.sheshapay.sheshapay.service.TransactionService;
import com.sheshapay.sheshapay.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class DataLoader {

    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final CardService cardService;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserService userService,
                      AccountService accountService,
                      TransactionService transactionService,
                      CardService cardService,
                      PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.cardService = cardService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            ObjectMapper mapper = new ObjectMapper();
            List<Account> allAccounts = new ArrayList<>();

            // === STEP 1: Load users from registers.json ===
            try (InputStream regStream = TypeReference.class.getResourceAsStream("/registers.json")) {
                if (regStream != null) {
                    List<RegisterForm> forms = mapper.readValue(regStream, new TypeReference<>() {});
                    for (RegisterForm form : forms) {
                        try {
                            // Register user and create account
                            var user = userService.registerUser(form);
                            var account = accountService.createAccount(user);
                            allAccounts.add(account);

                            // Starter deposit >= 2000
                            BigDecimal depositAmount = BigDecimal.valueOf(2000 + Math.random() * 8000)
                                    .setScale(2, BigDecimal.ROUND_HALF_UP);
                            transactionService.starterDeposit(depositAmount, user.getUsername(), "Initial deposit");
                            System.out.println("Created account for: " + form.getUsername());
                        } catch (Exception e) {
                            System.out.println("Skipping duplicate or invalid user: " + form.getUsername());
                        }
                    }
                    System.out.println(" Dummy customers & businesses inserted successfully.");
                } else {
                    System.out.println(" registers.json not found in resources.");
                }
            } catch (Exception e) {
                System.out.println(" Failed to load registers.json: " + e.getMessage());
            }

            // === STEP 2: Load card info from cardinfo.json ===
            try (InputStream cardStream = TypeReference.class.getResourceAsStream("/cardinfo.json")) {
                if (cardStream != null) {
                    List<Map<String, String>> cards = mapper.readValue(cardStream, new TypeReference<>() {});
                    for (Map<String, String> cardMap : cards) {
                        try {
                            CardForm cardForm = new CardForm();
                            cardForm.setUsername(cardMap.get("username"));
                            cardForm.setCardNo(cardMap.get("cardNo"));
                            cardForm.setExpiry(cardMap.get("expiry"));
                            cardForm.setBrand(cardMap.get("brand"));
                            cardForm.setCvv(cardMap.get("cvv"));

                            cardService.registerToken(cardForm.getUsername(), cardForm);
                            System.out.println("Registered card for user: " + cardForm.getUsername());
                        } catch (Exception e) {
                            System.out.println("Failed to register card for user " +
                                    cardMap.get("username") + ": " + e.getMessage());
                        }
                    }
                    System.out.println("All cards from cardinfo.json registered successfully.");
                } else {
                    System.out.println("cardinfo.json not found in resources.");
                }
            } catch (Exception e) {
                System.out.println(" Failed to load cardinfo.json: " + e.getMessage());
            }

            // === STEP 3: Generate transactions ===
            try {
                System.out.println("Generating real transactions...");
                transactionService.generateAndExecuteRealTransactions(allAccounts, 15);
                System.out.println("Real transactions generated successfully!");
            } catch (Exception e) {
                System.out.println("Transaction generation failed: " + e.getMessage());
            }
        };
    }
}
