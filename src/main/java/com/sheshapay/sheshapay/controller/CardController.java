package com.sheshapay.sheshapay.controller;

import com.sheshapay.sheshapay.dto.CardDTO;
import com.sheshapay.sheshapay.exception.FormException;
import com.sheshapay.sheshapay.form.CardForm;
import com.sheshapay.sheshapay.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/card")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Cards", description = "Endpoints for card registration and retrieval")
public class CardController {

    @Autowired
    private CardService cardService;

    @PostMapping("/register")
    @Operation(summary = "Register a card", description = "Registers a new card for the authenticated user and stores it as a token")
    public ResponseEntity<String> register(@RequestBody CardForm form, @AuthenticationPrincipal UserDetails user) {
        try {
            cardService.registerToken(user.getUsername(), form);
        } catch (FormException | UsernameNotFoundException e) {
            System.out.println(e.getClass());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Card Details updated Success");
    }

    @GetMapping("/info")
    @Operation(summary = "Get registered card", description = "Retrieves the registered card information for the authenticated user")
    public ResponseEntity<?> getCard(@AuthenticationPrincipal UserDetails user) {
        try {
            CardDTO cardInfo = cardService.getTokenForUser(user.getUsername());
            return ResponseEntity.ok(cardInfo);
        } catch (Exception e) {
            System.out.println(e.getClass() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No card registered for this user"));
        }
    }
}
