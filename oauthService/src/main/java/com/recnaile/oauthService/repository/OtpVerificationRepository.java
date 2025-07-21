package com.recnaile.oauthService.repository;

import com.recnaile.oauthService.model.OtpVerification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OtpVerificationRepository extends MongoRepository<OtpVerification, String> {
    Optional<OtpVerification> findByEmailAndOtpAndUsedFalse(String email, String otp);
    void deleteByEmail(String email);
}