package com.sheshapay.sheshapay.model;

import com.sheshapay.sheshapay.enums.ProfileType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProfileType profileType;

    private String firstName;
    private String lastName;

    private String businessName;
    private String category;

    private String phone;
    private String address;
}
