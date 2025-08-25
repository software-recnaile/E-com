package com.recnaile.authService.service;

import com.recnaile.authService.model.OtpVerification;
import com.recnaile.authService.model.PasswordResetToken;
import com.recnaile.authService.repository.OtpVerificationRepository;
import com.recnaile.authService.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {
    @Autowired
    private OtpVerificationRepository otpVerificationRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    @Value("${otp.expiration.minutes}")
    private int otpExpirationMinutes;

    public String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    public void sendOtpEmail(String email, String purpose) {
        String otp = generateOtp();
        Date expiryDate = new Date(System.currentTimeMillis() + (otpExpirationMinutes * 60 * 1000));

        if ("verification".equals(purpose)) {
            // For email verification
            otpVerificationRepository.deleteByEmail(email);

            OtpVerification otpVerification = new OtpVerification();
            otpVerification.setEmail(email);
            otpVerification.setOtp(otp);
            otpVerification.setExpiryDate(expiryDate);
            otpVerification.setUsed(false);
            otpVerificationRepository.save(otpVerification);
        } else if ("password_reset".equals(purpose)) {
            // For password reset
            passwordResetTokenRepository.deleteByEmail(email);

            PasswordResetToken passwordResetToken = new PasswordResetToken();
            passwordResetToken.setEmail(email);
            passwordResetToken.setOtp(otp);
            passwordResetToken.setExpiryDate(expiryDate);
            passwordResetToken.setUsed(false);
            passwordResetTokenRepository.save(passwordResetToken);
        }

        // Send email
        String subject = "Your OTP Code for " + purpose.replace("_", " ");
        String text = "Your OTP code is: " + otp + ". It will expire in " + otpExpirationMinutes + " minutes.";
        emailService.sendEmail(email, subject, text);
    }

    public boolean verifyOtp(String email, String otp, String purpose) {
        if ("verification".equals(purpose)) {
            Optional<OtpVerification> otpVerification = otpVerificationRepository.findByEmailAndOtpAndUsedFalse(email, otp);
            if (otpVerification.isPresent()) {
                OtpVerification verification = otpVerification.get();
                if (verification.getExpiryDate().after(new Date())) {
                    verification.setUsed(true);
                    otpVerificationRepository.save(verification);
                    return true;
                }
            }
        } else if ("password_reset".equals(purpose)) {
            Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByEmailAndOtpAndUsedFalse(email, otp);
            if (passwordResetToken.isPresent()) {
                PasswordResetToken token = passwordResetToken.get();
                if (token.getExpiryDate().after(new Date())) {
                    token.setUsed(true);
                    passwordResetTokenRepository.save(token);
                    return true;
                }
            }
        }
        return false;
    }
}