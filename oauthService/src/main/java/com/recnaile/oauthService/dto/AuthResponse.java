package com.recnaile.oauthService.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String email;
    private String username;
}