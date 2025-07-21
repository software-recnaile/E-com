//package com.recnaile.authService.model;
//
//import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.util.Date;
//
//@Data
//@Document(collection = "password_reset_tokens")
//public class PasswordResetToken {
//    @Id
//    private String id;
//    private String email;
//    private String token;
//    private Date expiryDate;
//    private boolean used;
//}

package com.recnaile.authService.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    private String id;
    private String email;
    private String otp;
    private Date expiryDate;
    private boolean used;
}