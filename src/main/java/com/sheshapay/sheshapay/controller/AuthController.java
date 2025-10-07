package com.sheshapay.sheshapay.controller;

import com.sheshapay.sheshapay.enums.UserRole;
import com.sheshapay.sheshapay.exception.FormException;
import com.sheshapay.sheshapay.exception.UserExists;
import com.sheshapay.sheshapay.form.LoginForm;
import com.sheshapay.sheshapay.form.RegisterForm;
import com.sheshapay.sheshapay.model.User;
import com.sheshapay.sheshapay.repo.UserRepository;
import com.sheshapay.sheshapay.security.JwtService;
import com.sheshapay.sheshapay.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and management")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with the given details")
    public ResponseEntity<?> registerUser(@RequestBody RegisterForm form) {
        try {
            RegisterForm.validate(form);

            userService.registerUser(form);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("status", "success", "message", "User registered successfully")
            );
        } catch (UserExists e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("status", "error", "message", "User already exists")
            );
        } catch (FormException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("status", "error", "message", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("status", "error", "message", e.getMessage())
            );
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates a user with email and password, returns a JWT token")
    public ResponseEntity<?> login(@RequestBody LoginForm form, HttpServletResponse response) {
        try {
            User user = userRepository.findByEmail(form.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), form.getPassword())
            );

            String token = jwtService.generateToken(user.getUsername());

            ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(2 * 60 * 60)
                    .sameSite("lax")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

            Map<String, Object> body = new HashMap<>();
            body.put("token", token);
            body.put("expires_in", 2 * 60 * 60);
            body.put("username", user.getUsername());

            return ResponseEntity.ok(body);

        } catch (Exception e) {
            System.out.println(e.getClass());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email/username or password"));
        }
    }

    @GetMapping("/users")
    @Operation(summary = "List all users", description = "Fetches a list of all registered users")
    public ResponseEntity<?> getUsers() {
        try {
            return ResponseEntity.ok(userRepository.findAll());
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Could not fetch users");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }



}
