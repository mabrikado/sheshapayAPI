package com.sheshapay.sheshapay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {
    private String username;
    private String cardNumber;
    private String expiry;
    private String brand;
    private String cvv;
}
