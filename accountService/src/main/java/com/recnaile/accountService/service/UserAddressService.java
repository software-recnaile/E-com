package com.recnaile.accountService.service;

import com.recnaile.accountService.exception.ResourceNotFoundException;
import com.recnaile.accountService.model.UserAddresses;
import com.recnaile.accountService.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAddressService {
    private final AddressRepository addressRepository;

    public UserAddresses getAddress(String userId) {
        return addressRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found for user"));
    }

    public UserAddresses.Address getAddressById(String userId, String addressId) {
        UserAddresses userAddresses = getAddress(userId);

        return userAddresses.getAddresses().stream()
                .filter(addr -> addr.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

    public UserAddresses.Address getDefaultAddress(String userId) {
        UserAddresses userAddresses = getAddress(userId);

        return userAddresses.getAddresses().stream()
                .filter(UserAddresses.Address::isDefault)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No default address found"));
    }

    public boolean verifyAddressBelongsToUser(String userId, String addressId) {
        try {
            getAddressById(userId, addressId);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
}