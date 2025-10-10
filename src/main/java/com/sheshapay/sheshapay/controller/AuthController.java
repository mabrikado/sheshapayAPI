package com.sheshapay.sheshapay.controller;

import com.sheshapay.sheshapay.enums.UserRole;
import com.sheshapay.sheshapay.exception.FormException;
import com.sheshapay.sheshapay.exception.UserExists;
import com.sheshapay.sheshapay.form.LoginForm;
import com.sheshapay.sheshapay.form.RegisterForm;
import com.sheshapay.sheshapay.model.User;
import com.sheshapay.sheshapay.repo.UserRepository;
import com.sheshapay.sheshapay.security.JwtService;
import com.sheshapay.sheshapay.service.AccountService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and management")
@CrossOrigin(origins = "", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;



    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<?> registerUser(@RequestBody RegisterForm form) {
        try {
            RegisterForm.validate(form);
            User user = userService.registerUser(form); //register User
            accountService.createAccount(user); // Create Account
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("status", "success", "message", "User registered successfully"));
        } catch (UserExists e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", "error", "message", "User already exists"));
        } catch (FormException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
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
                    .sameSite("Lax")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

            Map<String, Object> body = new HashMap<>();
            body.put("token", token);
            body.put("expires_in", 2 * 60 * 60);
            body.put("username", user.getUsername());

            return ResponseEntity.ok(body);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        try {
            return ResponseEntity.ok(userRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not fetch users", "message", e.getMessage()));
        }
    }

    @GetMapping("/logged-in")
    public ResponseEntity<?> getLoggedInUser(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "No user logged in"));
            }
            return ResponseEntity.ok(Map.of("username", userDetails.getUsername()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not fetch user"));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Clears the JWT cookie and ends the session")
    public ResponseEntity<?> logOut(HttpServletResponse response) {
        ResponseCookie clearCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
