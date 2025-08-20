//
//
//package com.recnaile.accountService.model;
//
//import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import javax.validation.constraints.NotBlank;
//import java.util.List;
//
//@Data
//@Document(collection = "addresses")
//public class UserAddresses {
//    @Id
//    private String id;
//
//    @NotBlank(message = "User ID is required")
//    private String userId;
//
//    private List<Address> addresses;
//
//    @Data
//    public static class Address {
//        @NotBlank(message = "Street is required")
//        private String street;
//
//        @NotBlank(message = "District is required")
//        private String district;
//
//        @NotBlank(message = "State is required")
//        private String state;
//
//        @NotBlank(message = "Pincode is required")
//        private String pincode;
//
//        private String landmark;
//        private boolean isDefault;
//
//
//    }
//}

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