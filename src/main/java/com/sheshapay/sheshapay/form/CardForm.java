package com.sheshapay.sheshapay.form;

import com.sheshapay.sheshapay.exception.FormException;
import lombok.Data;

@Data
public class CardForm {
    private String username;
    private String cardNo;
    private String expiry;
    private String brand;
    private String cvv;
    private String firstName;
    private String lastName;

    public void validate() throws FormException {
        if (cardNo == null || !cardNo.matches("\\d{13,19}")) {
            throw new FormException("Invalid card number format.");
        }
        if (!isValidLuhn(cardNo)) {
            throw new FormException("Card number failed Luhn check.");
        }

        if (expiry == null || !expiry.matches("^(0[1-9]|1[0-2])/(\\d{2}|\\d{4})$")) {
            throw new FormException("Invalid expiry format. Use MM/YY or MM/YYYY.");
        }

        if (cvv == null || !cvv.matches("^\\d{3,4}$")) {
            throw new FormException("Invalid CVV format.");
        }

        if (brand == null || brand.trim().isEmpty()) {
            throw new FormException("Card brand is required.");
        }
    }

    private boolean isValidLuhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }
}
