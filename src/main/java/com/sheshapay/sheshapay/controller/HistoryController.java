package com.sheshapay.sheshapay.controller;

import com.sheshapay.sheshapay.service.HistoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/history")
@CrossOrigin(origins = "")
@Tag(name = "History", description = "A history controller to get past activities of Users")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @GetMapping("/paginated")
    public ResponseEntity<?> getHistory(@AuthenticationPrincipal UserDetails userDetails ,
         @RequestParam(defaultValue = "0") int page,
         @RequestParam(defaultValue = "10") int size){
        try {
            return ResponseEntity.ok().body(
                    Map.of("history"  , historyService.getUserHistory(userDetails.getUsername(), page, size))
            );
        }catch (Exception e){
            return ResponseEntity.badRequest().body(
                    Map.of("message" ,  e.getMessage())
            );
        }
    }
}
