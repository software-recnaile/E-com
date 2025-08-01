package com.recnaile.address.controller;


import com.recnaile.address.model.UserAddresses;
import com.recnaile.address.service.AddressService;
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
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.addAddress(userId, address));
    }
    // Get all addresses for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserAddresses> getUserAddresses(@PathVariable String userId) {
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }

    // Use this for PUT
    @PutMapping("/user/{userId}/address/{addressId}")
    public ResponseEntity<UserAddresses> updateAddress(
            @PathVariable String userId,
            @PathVariable String addressId,
            @Valid @RequestBody UserAddresses.Address updatedAddress) {
        return ResponseEntity.ok(addressService.updateAddress(userId, addressId, updatedAddress));
    }

    // Use this for DELETE
    @DeleteMapping("/user/{userId}/address/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable String userId,
            @PathVariable String addressId) {
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }

    // Delete all addresses for a user
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteAllAddresses(@PathVariable String userId) {
        addressService.deleteAllAddresses(userId);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/user/{userId}/address/{addressId}/toggle-default")
public ResponseEntity<UserAddresses> toggleDefaultAddress(
        @PathVariable String userId,
        @PathVariable String addressId) {
    return ResponseEntity.ok(addressService.toggleDefaultAddress(userId, addressId));
}
    @GetMapping("/user/{userId}/address/{addressId}")
public ResponseEntity<UserAddresses.Address> getAddressById(
        @PathVariable String userId,
        @PathVariable String addressId) {
    return ResponseEntity.ok(addressService.getAddressById(userId, addressId));
}

}
