package com.SecureAuth.SecureAuth.Api.repository;

import com.SecureAuth.SecureAuth.Api.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {

    // find the valid otp by email and code
    Optional<OTP> findByEmailAndOtpCodeAndUsedFalse(String email, String otpCode);

    // delete all the otp dude
    void deleteByEmail(String email);

    // finding the latest otp of a particular email
    Optional<OTP> findFirstByEmailOrderByCreatedAtDesc(String email);
}
