package com.SecureAuth.SecureAuth.Api.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    // making primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    @Column(nullable = false)
    private String name;

    @Column(name = "profile_picture")
    private String profilePicture;

    // which is filler by the provider , for providing knowledge about in which
    // method the user is verified

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "email_verified")
    private boolean emailVerified = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum AuthProvider {
        LOCAL,
        GOOGLE,
        GITHUB

    }
}
