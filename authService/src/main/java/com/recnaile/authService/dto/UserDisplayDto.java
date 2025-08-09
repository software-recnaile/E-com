package com.recnaile.authService.dto;

import lombok.Data;

@Data
public class UserDisplayDto {
    private String username;
    private String email;
    private boolean isAdmin;
}