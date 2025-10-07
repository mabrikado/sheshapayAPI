package com.sheshapay.sheshapay.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<?> dashBoardInfo(@AuthenticationPrincipal UserDetails user) {

        return ResponseEntity.ok(user.getUsername());
    }


}
