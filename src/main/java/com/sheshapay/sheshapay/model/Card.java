package com.sheshapay.sheshapay.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "card_tokens")
@Data
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String expiryDate;
    private String cardNumber;
    private String brand;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

}
