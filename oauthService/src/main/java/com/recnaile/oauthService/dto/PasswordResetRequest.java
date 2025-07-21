//package com.recnaile.authService.dto;
//
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import lombok.Data;
//
//@Data
//public class PasswordResetRequest {
//    @NotBlank
//    @Email
//    private String email;
//}

package com.recnaile.oauthService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequest {
    @NotBlank
    @Email
    private String email;
}