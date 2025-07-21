package com.recnaile.oauthService.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "otp_verifications")
public class OtpVerification {
    @Id
    private String id;
    private String email;
    private String otp;
    private Date expiryDate;
    private boolean used;
}