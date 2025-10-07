package com.sheshapay.sheshapay.dto;

import com.sheshapay.sheshapay.enums.AccountType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AccountDTO {
    private String accountNumber;
    private LocalDate registered;
    private BigDecimal balance;
    private AccountType accountType;
}
