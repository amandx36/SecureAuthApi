package com.SecureAuth.SecureAuth.Api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
@Data
public class OTP {

    // defining the enum type dude
    public enum OTPType {
        LOGIN,
        REGISTER,
        PASSWORD_RESET
    }

    // making the primary key dude
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(name = "otp_code", length = 6)
    private String otpCode;

    @Enumerated(EnumType.STRING)
    private OTPType type;

    // data type for storing the expiry time

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    private boolean used = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // function to check is the otp is expired dude !!
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    // function to check is the otp is valid or not
    public boolean isValid() {
        return !used && !isExpired();
    }

}
