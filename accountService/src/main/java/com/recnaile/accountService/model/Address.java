package com.recnaile.accountService.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Document(collection = "addresses")
public class Address {
    @Id
    private String id;

    @Field("userId")
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Street is required")
    private String street;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Pincode is required")
    private String pincode;

    private String landmark;

    private boolean isDefault;

    private List<Address> addressList; // For storing multiple addresses
}