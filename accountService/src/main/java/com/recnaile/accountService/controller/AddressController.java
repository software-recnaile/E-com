//package com.recnaile.accountService.controller;
//
//import com.recnaile.accountService.model.Address;
//import com.recnaile.accountService.service.AddressService;
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/addresses")
//public class AddressController {
//    private final AddressService addressService;
//
//    public AddressController(AddressService addressService) {
//        this.addressService = addressService;
//    }
//
//    @PostMapping
//    public ResponseEntity<Address> addAddress(@Valid @RequestBody Address address) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.addAddress(address));
//    }
//
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Address>> getUserAddresses(@PathVariable String userId) {
//        return ResponseEntity.ok(addressService.getUserAddresses(userId));
//    }
//
//    @GetMapping("/{id}/user/{userId}")
//    public ResponseEntity<Address> getAddress(@PathVariable String id, @PathVariable String userId) {
//        return ResponseEntity.ok(addressService.getAddress(id, userId));
//    }
//
//    @PutMapping("/{id}/user/{userId}")
//    public ResponseEntity<Address> updateAddress(
//            @PathVariable String id,
//            @PathVariable String userId,
//            @Valid @RequestBody Address updatedAddress) {
//        return ResponseEntity.ok(addressService.updateAddress(id, userId, updatedAddress));
//    }
//
//    @DeleteMapping("/{id}/user/{userId}")
//    public ResponseEntity<Void> deleteAddress(@PathVariable String id, @PathVariable String userId) {
//        addressService.deleteAddress(id, userId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PatchMapping("/{id}/user/{userId}/set-default")
//    public ResponseEntity<Address> setDefaultAddress(@PathVariable String id, @PathVariable String userId) {
//        return ResponseEntity.ok(addressService.setDefaultAddress(id, userId));
//    }
//}

package com.recnaile.accountService.controller;

import com.recnaile.accountService.model.UserAddresses;
import com.recnaile.accountService.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // Create or update all addresses for a user
    @PostMapping("/user/{userId}")
    public ResponseEntity<UserAddresses> saveAllAddresses(
            @PathVariable String userId,
            @Valid @RequestBody List<UserAddresses.Address> addresses) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.createOrUpdateAddresses(userId, addresses));
    }

    // Add a single address to user's addresses
    @PostMapping("/user/{userId}/add")
    public ResponseEntity<UserAddresses> addAddress(
            @PathVariable String userId,
            @Valid @RequestBody UserAddresses.Address address) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.addAddress(userId, address));
    }

    // Get all addresses for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserAddresses> getUserAddresses(@PathVariable String userId) {
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }

    // Update a specific address by index
    @PutMapping("/user/{userId}/{addressIndex}")
    public ResponseEntity<UserAddresses> updateAddress(
            @PathVariable String userId,
            @PathVariable String addressIndex,
            @Valid @RequestBody UserAddresses.Address updatedAddress) {
        return ResponseEntity.ok(addressService.updateAddress(userId, addressIndex, updatedAddress));
    }

    // Delete a specific address by index
    @DeleteMapping("/user/{userId}/{addressIndex}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable String userId,
            @PathVariable String addressIndex) {
        addressService.deleteAddress(userId, addressIndex);
        return ResponseEntity.noContent().build();
    }

    // Delete all addresses for a user
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteAllAddresses(@PathVariable String userId) {
        addressService.deleteAllAddresses(userId);
        return ResponseEntity.noContent().build();
    }
}