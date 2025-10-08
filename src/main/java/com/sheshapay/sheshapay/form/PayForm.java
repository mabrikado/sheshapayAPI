package com.sheshapay.sheshapay.form;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayForm {
    private BigDecimal amount;
    private String businessName;
    private String reference;
}
