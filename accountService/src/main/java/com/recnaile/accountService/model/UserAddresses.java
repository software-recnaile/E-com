package com.recnaile.accountService.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@Document(collection = "addresses")
public class UserAddresses {
    @Id
    private String id;
    private String userId;
    private List<Address> addresses;

    @Data
    public static class Address {
        private String id;
        private String street;
        private String district;
        private String state;
        private String pincode;
        private String landmark;
        private boolean isDefault;

        public Address() {
            this.id = UUID.randomUUID().toString();
        }
    }
}