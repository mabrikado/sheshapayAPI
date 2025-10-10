package com.sheshapay.sheshapay.model;

import com.sheshapay.sheshapay.enums.HistoryType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "history")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private HistoryType historyType;
    private String action;
    private LocalDateTime date;
}
