package com.sheshapay.sheshapay.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");

        if (statusCode != null && statusCode == 404) {
            String path = (String) request.getAttribute("jakarta.servlet.error.request_uri");
            if (path != null && !path.startsWith("/account")
                    && !path.startsWith("/card")
                    && !path.startsWith("/history")
                    && !path.startsWith("/profile")
                    && !path.startsWith("/transactions")
                    && !path.startsWith("/api")) {
                return "forward:/";
            }
        }
        return "index";
    }
}
