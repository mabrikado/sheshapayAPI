package com.sheshapay.sheshapay.controller;

import aj.org.objectweb.asm.Handle;
import com.sheshapay.sheshapay.dto.AccountDTO;
import com.sheshapay.sheshapay.form.RegisterForm;
import com.sheshapay.sheshapay.service.AccountService;
import com.sheshapay.sheshapay.service.DashBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController()
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private DashBoardService dashBoardService;

    @GetMapping("/")
    public ResponseEntity<AccountDTO> getAccount(@AuthenticationPrincipal UserDetails user){
        try {
            AccountDTO dto = accountService.getAccountInfo(user.getUsername());
            return ResponseEntity.ok(dto);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<HashMap<String , Object>> getDashboardInfo(@AuthenticationPrincipal UserDetails user){
        try {
            dashBoardService.getDashBoardInfo(user.getUsername());
            return ResponseEntity.ok(dashBoardService.getDashBoardInfo(user.getUsername()));
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
}
