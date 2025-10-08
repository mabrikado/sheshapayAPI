package com.sheshapay.sheshapay.dto;

import com.sheshapay.sheshapay.enums.TransactionType;
import com.sheshapay.sheshapay.model.Transaction;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class TransactionDTO {
    private Long id;
    private String fromAccount;
    private String toAccount;
    private String amount;
    private String externalSource;
    private TransactionType type;
    private LocalDateTime timestamp;

    public static TransactionDTO fromEntity(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setFromAccount(transaction.getFromAccount() != null ? transaction.getFromAccount().getAccountNumber() : null);
        dto.setToAccount(transaction.getToAccount() != null ? transaction.getToAccount().getAccountNumber() : null);
        dto.setAmount(transaction.getAmount().toString());
        dto.setExternalSource(transaction.getExternalSource());
        dto.setType(transaction.getType());
        dto.setTimestamp(transaction.getTimestamp());
        return dto;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TransactionDTO that = (TransactionDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(fromAccount, that.fromAccount) && Objects.equals(toAccount, that.toAccount) && Objects.equals(amount, that.amount) && Objects.equals(externalSource, that.externalSource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromAccount, toAccount, amount, externalSource);
    }
}
