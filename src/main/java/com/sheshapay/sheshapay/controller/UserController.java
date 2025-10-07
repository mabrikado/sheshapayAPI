package com.sheshapay.sheshapay.controller;

import com.sheshapay.sheshapay.model.User;
import com.sheshapay.sheshapay.repo.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoints for managing users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Fetches all registered users from the database")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/check-username")
    @Operation(summary = "Check if username exists", description = "Returns 200 if username exists, 404 otherwise")
    public ResponseEntity<?> usernameExist(@RequestParam String username) {
        if (username.length() < 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Username must be at least 5 characters"));
        }

        boolean exists = userRepository.findByUsername(username).isPresent();
        if (exists) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "error", "message", "Username already exists"));
        }
        return ResponseEntity.ok(Map.of("status", "success", "message", "Username is available"));
    }

    @GetMapping("/check-email")
    @Operation(summary = "Check if email exists", description = "Returns 200 if email exists, 404 otherwise")
    public ResponseEntity<?> emailExist(@RequestParam String email) {
        boolean exists = userRepository.findByEmail(email).isPresent();
        if (exists) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "error", "message", "Email already exists"));
        }
        return ResponseEntity.ok(Map.of("status", "success", "message", "Email is available"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestParam String username) {
        try {
            return ResponseEntity.ok(userRepository.findByUsername(username));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Could not fetch user");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
