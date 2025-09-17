package com.sheshapay.sheshapay.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheshapay.sheshapay.model.User;
import com.sheshapay.sheshapay.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.InputStream;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<User>> typeReference = new TypeReference<>() {};
            InputStream inputStream = TypeReference.class.getResourceAsStream("/users.json");

            try {
                List<User> users = mapper.readValue(inputStream, typeReference);
                for (User user : users) {
                    if (userRepository.findByUsername(user.getUsername()).isEmpty()) {
                        // encode password before saving
                        user.setPassword(passwordEncoder.encode(user.getPassword()));
                        userRepository.save(user);
                    }
                }
                System.out.println("Dummy users inserted");
            } catch (Exception e) {
                System.out.println("Failed to load dummy users: " + e.getMessage());
            }
        };
    }
}
