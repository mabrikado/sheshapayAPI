package com.sheshapay.sheshapay.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheshapay.sheshapay.form.CardForm;
import com.sheshapay.sheshapay.form.RegisterForm;
import com.sheshapay.sheshapay.model.User;
import com.sheshapay.sheshapay.repo.UserRepository;
import com.sheshapay.sheshapay.service.AccountService;
import com.sheshapay.sheshapay.service.CardService;
import com.sheshapay.sheshapay.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.InputStream;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   UserService userService,
                                   CardService cardService,
                                   AccountService accountService) {  // 🧩 Inject AccountService
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
                            accountService.createAccount(admin); // 👈 Also create accounts for admins
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
}
