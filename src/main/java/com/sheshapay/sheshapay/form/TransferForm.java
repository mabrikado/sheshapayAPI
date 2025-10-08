package com.sheshapay.sheshapay.form;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferForm {
    private String toUsername;
    private BigDecimal amount;
    private String reference;
}
