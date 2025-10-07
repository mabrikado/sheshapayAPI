package com.sheshapay.sheshapay.form;

import com.sheshapay.sheshapay.enums.AccountType;
import com.sheshapay.sheshapay.enums.ProfileType;
import com.sheshapay.sheshapay.enums.UserRole;
import com.sheshapay.sheshapay.exception.FormException;
import lombok.Data;

@Data
public class RegisterForm {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String phone;
    private ProfileType profileType;
    private String businessName;
    private String address;
    private UserRole role;

    public static void validate(RegisterForm form) throws FormException {
        if (form.getFirstName() == null || form.getFirstName().isBlank()) {
            throw new FormException("First name is required");
        }

        if (form.getLastName() == null || form.getLastName().isBlank()) {
            throw new FormException("Last name is required");
        }

        if (form.getUsername() == null || form.getUsername().isBlank()) {
            throw new FormException("Username is required");
        }

        if (form.getPassword() == null || form.getPassword().isBlank()) {
            throw new FormException("Password is required");
        }

        if (form.getEmail() == null || form.getEmail().isBlank()) {
            throw new FormException("Email is required");
        }

        if (form.getProfileType() == null) {
            throw new FormException("Profile type is required");
        }

        if (form.getProfileType().equals(ProfileType.BUSINESS)) {
            if (form.getBusinessName() == null || form.getBusinessName().isBlank()) {
                throw new FormException("Business name is required for business profile");
            }
        }
    }


}
