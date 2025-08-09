package com.recnaile.authService.dto;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class AuthResponse {
    private String token;
    private String email;
    private String username;
    private String id;
    public void setId(ObjectId id) {
        this.id = id.toString(); // or just store as ObjectId
    }
}
