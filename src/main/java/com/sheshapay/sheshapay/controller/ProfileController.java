package com.sheshapay.sheshapay.controller;

import com.sheshapay.sheshapay.dto.ProfileDTO;
import com.sheshapay.sheshapay.form.ProfileForm;
import com.sheshapay.sheshapay.service.ProfileService;
import com.sheshapay.sheshapay.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/profile")
@Tag(name = "Profile", description = "Endpoints for user Profile (view,update)")
@CrossOrigin(origins = "http://localhost:3000")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/info")
    public ResponseEntity<ProfileDTO> getProfile(@AuthenticationPrincipal UserDetails user) {
        try {
            ProfileDTO dto = profileService.getProfile(user.getUsername());
            return ResponseEntity.ok(dto);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateProfile(@AuthenticationPrincipal UserDetails user, @RequestBody ProfileForm form) {
        try {
            profileService.updateProfile(form , user.getUsername());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

}
