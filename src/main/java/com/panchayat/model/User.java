package com.panchayat.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** A society resident or an admin/committee member. */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    private String phone;

    /** e.g. "A-204" */
    private String flatNumber;

    @NotBlank
    private String password; // stored BCrypt-encoded

    @Enumerated(EnumType.STRING)
    private Role role = Role.RESIDENT;

    /** Committee role for ADMIN accounts (e.g. Secretary, Treasurer, Security In-Charge, General Admin) */
    private String committeeRole;

    /** Whether the account is approved. Residents default to true; Admin signups default to false until approved by existing admin. */
    private boolean approved = true;

    private LocalDateTime createdAt = LocalDateTime.now();
}
