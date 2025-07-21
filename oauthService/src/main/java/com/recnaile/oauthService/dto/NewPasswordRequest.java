package com.recnaile.oauthService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewPasswordRequest {
    @NotBlank
    private String token;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String confirmPassword;
    @NotBlank
    private String email;


}