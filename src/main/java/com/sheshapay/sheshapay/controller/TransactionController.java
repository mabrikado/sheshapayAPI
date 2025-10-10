package com.sheshapay.sheshapay.controller;

import com.sheshapay.sheshapay.form.DepositWithdrawForm;
import com.sheshapay.sheshapay.form.PayForm;
import com.sheshapay.sheshapay.form.TransactionsFormDate;
import com.sheshapay.sheshapay.form.TransferForm;
import com.sheshapay.sheshapay.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Authentication", description = "Endpoints for user Profile (view,update)")
@CrossOrigin(origins = "")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@AuthenticationPrincipal UserDetails userDetails , @RequestBody DepositWithdrawForm form){
        try {
            transactionService.deposit(form.getAmount(), userDetails.getUsername());
            return ResponseEntity.ok().build();
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("message" , ex.getMessage())
            );
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@AuthenticationPrincipal UserDetails userDetails , @RequestBody DepositWithdrawForm form){
        try {
            transactionService.withdraw(form.getAmount(), userDetails.getUsername());
            return ResponseEntity.ok().build();
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("message" , ex.getMessage())
            );
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@AuthenticationPrincipal UserDetails userDetails , @RequestBody TransferForm form){
        try {
            transactionService.transfer(form.getAmount() , userDetails.getUsername() , form.getToUsername(),  form.getReference());
            return ResponseEntity.ok().build();
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("message" , ex.getMessage())
            );
        }
    }

    @PostMapping("/pay")
    public ResponseEntity<?> pay(@AuthenticationPrincipal UserDetails userDetails , @RequestBody PayForm form){
        try {
            transactionService.pay(form.getAmount() , userDetails.getUsername() , form.getBusinessName(), form.getReference());
            return ResponseEntity.ok().build();
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("message" , ex.getMessage())
            );
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        try{
            return ResponseEntity.ok().body(
                    Map.of("transactions" , transactionService.getTransactions(userDetails.getUsername() , 0)));
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("message" , ex.getMessage())
            );
        }
    }

    @GetMapping("/paginated")
    public ResponseEntity<?> getPaginatedTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            return ResponseEntity.ok().body(
                    Map.of("transactions", transactionService.getTransactions(userDetails.getUsername(), page, size))
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("message", ex.getMessage())
            );
        }
    }
}
