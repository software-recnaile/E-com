package com.recnaile.oauthService.controller;

import com.recnaile.oauthService.dto.*;
import com.recnaile.oauthService.service.AuthService;
import com.recnaile.oauthService.service.OtpService;
import com.recnaile.oauthService.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(userService.registerUser(signUpRequest));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@Valid @RequestBody OtpRequest otpRequest) {
        if (otpService.verifyOtp(otpRequest.getEmail(), otpRequest.getOtp(), "verification")) {
            userService.verifyUser(otpRequest.getEmail());
            return ResponseEntity.ok("User verified successfully");
        }
        return ResponseEntity.badRequest().body("Invalid OTP or OTP expired");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        userService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok("Password reset OTP sent to your email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }

        if (!otpService.verifyOtp(request.getEmail(), request.getOtp(), "password_reset")) {
            return ResponseEntity.badRequest().body("Invalid OTP or OTP expired");
        }

        userService.resetPassword(request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }
    @GetMapping("/display")
    public String display(){
        return "Docker file 8081";
    }
}
