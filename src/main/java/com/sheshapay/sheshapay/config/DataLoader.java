package com.sheshapay.sheshapay.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheshapay.sheshapay.form.CardForm;
import com.sheshapay.sheshapay.form.RegisterForm;
import com.sheshapay.sheshapay.model.User;
import com.sheshapay.sheshapay.repo.UserRepository;
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
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   UserService userService,
                                   CardService cardService,
                                   AccountService accountService,
                                   TransactionService transactionService) {
        return args -> {
            ObjectMapper mapper = new ObjectMapper();

            // Load customers & businesses from registers.json
            try (InputStream regStream = TypeReference.class.getResourceAsStream("/registers.json")) {
                if (regStream != null) {
                    List<RegisterForm> forms = mapper.readValue(regStream, new TypeReference<>() {});
                    for (RegisterForm form : forms) {
                        try {
                            User user = userService.registerUser(form);
                            accountService.createAccount(user);

                            // Deposit random amount >= 2000
                            BigDecimal depositAmount = getRandomAmount(2000, 10000);
                            transactionService.starterDeposit(depositAmount, user.getUsername() , "Initial Deposit");

                        } catch (Exception e) {
                            System.out.println("Skipping duplicate register: " + form.getUsername());
                        }
                    }
                    System.out.println("Dummy customers & businesses inserted");
                }
            } catch (Exception e) {
                System.out.println("Failed to load registers.json: " + e.getMessage());
            }

            // Load admins from users.json
            try (InputStream userStream = TypeReference.class.getResourceAsStream("/users.json")) {
                if (userStream != null) {
                    List<User> admins = mapper.readValue(userStream, new TypeReference<>() {});
                    for (User admin : admins) {
                        if (userRepository.findByUsername(admin.getUsername()).isEmpty()) {
                            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
                            userRepository.save(admin);
                            accountService.createAccount(admin);

                            BigDecimal depositAmount = getRandomAmount(2000, 10000);
                            transactionService.starterDeposit(depositAmount, admin.getUsername() , "Initial Deposit");
                        }
                    }
                    System.out.println("Admin users inserted");
                }
            } catch (Exception e) {
                System.out.println("Failed to load users.json: " + e.getMessage());
            }

            // Load card info from cardinfo.json
            try (InputStream cardStream = TypeReference.class.getResourceAsStream("/cardinfo.json")) {
                if (cardStream != null) {
                    List<CardForm> cards = mapper.readValue(cardStream, new TypeReference<>() {});
                    for (CardForm cardForm : cards) {
                        try {
                            cardService.registerToken(cardForm.getUsername(), cardForm);
                        } catch (Exception e) {
                            System.out.println("Skipping card for user: " + cardForm.getUsername() +
                                    " because " + e.getMessage());
                        }
                    }
                    System.out.println("Card tokens inserted");
                }
            } catch (Exception e) {
                System.out.println("Failed to load cardinfo.json: " + e.getMessage());
            }
        };
    }

    private BigDecimal getRandomAmount(int min, int max) {
        double random = ThreadLocalRandom.current().nextDouble(min, max);
        return BigDecimal.valueOf(random).setScale(2, RoundingMode.HALF_UP);
    }
}
