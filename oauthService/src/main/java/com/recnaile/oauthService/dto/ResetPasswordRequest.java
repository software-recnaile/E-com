package com.recnaile.oauthService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String otp;

    @NotBlank
    @Size(min = 6, max = 100)
    private String newPassword;

    @NotBlank
    @Size(min = 6, max = 100)
    private String confirmPassword;
}