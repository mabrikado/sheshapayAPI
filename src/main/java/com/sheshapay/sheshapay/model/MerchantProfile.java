package com.sheshapay.sheshapay.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "merchant_profiles")
public class MerchantProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String businessName;

    @Column(unique = true, nullable = false)
    private String businessPhone;

    private String businessAddress;

    private String website;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
