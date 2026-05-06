package com.sreeraj.TMS.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String fullName;

    @Column(unique = true)
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$",
            message = "Password must contain at least one digit, one uppercase, and one lowercase letter")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean verified;

    private LocalDateTime createdAt;
}
