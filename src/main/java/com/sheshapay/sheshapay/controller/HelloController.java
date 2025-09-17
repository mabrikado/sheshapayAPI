package com.sheshapay.sheshapay.controller;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


@RestController
public class HelloController {

    @GetMapping("/")
    public String helloWorld(@AuthenticationPrincipal UserDetails user){
        return "Hello World" + user.getUsername();
    }
}
