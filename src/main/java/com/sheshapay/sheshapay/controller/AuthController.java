package com.sheshapay.sheshapay.controller;

import com.sheshapay.sheshapay.enums.UserRole;
import com.sheshapay.sheshapay.form.LoginForm;
import com.sheshapay.sheshapay.model.User;
import com.sheshapay.sheshapay.repo.UserRepository;
import com.sheshapay.sheshapay.security.JwtService;
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
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/")
    public String hello() {
        return "Hello World!";
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {

        try {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole(UserRole.CUSTOMER);
        }

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
        }
        catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data format");
        }

        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!");
        }
    }

    @PostMapping("/login")
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
                    .secure(true)
                    .path("/")
                    .maxAge(2 * 60 * 60)
                    .sameSite("Strict")
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
            Map<String, String> error = new HashMap<>();
            error.put("error", "Could not fetch users");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


}
