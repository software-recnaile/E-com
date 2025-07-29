package com.recnaile.address.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Document(collection = "addresses")
public class UserAddresses {
    @Id
    private String id;

    @NotBlank(message = "User ID is required")
    private String userId;

    private List<Address> addresses = new ArrayList<>();

    @Data
    public static class Address {
        private String id = UUID.randomUUID().toString();

        @NotBlank(message = "Street is required")
        private String street;

        @NotBlank(message = "District is required")
        private String district;

        @NotBlank(message = "State is required")
        private String state;

        @Pattern(regexp = "\\d{6}", message = "Pincode must be 6 digits")
        private String pincode;

        private String landmark;

        private boolean isDefault;
    }
}