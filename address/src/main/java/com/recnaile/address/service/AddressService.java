package com.recnaile.address.service;

import com.recnaile.address.exception.ResourceNotFoundException;
import com.recnaile.address.model.UserAddresses;
import com.recnaile.address.repository.AddressRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {
    private final AddressRepository repository;

    public UserAddresses getUserAddresses(String userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No addresses found for user"));
    }

    public UserAddresses.Address getDefaultAddress(String userId) {
        UserAddresses userAddresses = getUserAddresses(userId);
        
        return userAddresses.getAddresses().stream()
                .filter(UserAddresses.Address::isDefault)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No default address found for user"));
    }

    public UserAddresses createOrUpdateAddresses(String userId, List<UserAddresses.Address> addressList) {
        validateAddressList(addressList);
        validateDefaultAddress(addressList);

        UserAddresses existing = repository.findByUserId(userId).orElse(null);

        if (existing != null) {
            existing.setAddresses(addressList);
            return repository.save(existing);
        } else {
            UserAddresses newAddresses = new UserAddresses();
            newAddresses.setUserId(userId);
            newAddresses.setAddresses(addressList);
            return repository.save(newAddresses);
        }
    }

    public UserAddresses addAddress(String userId, UserAddresses.Address newAddress) {
        if (newAddress == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }

        UserAddresses userAddresses = repository.findByUserId(userId)
                .orElseGet(() -> {
                    UserAddresses newDoc = new UserAddresses();
                    newDoc.setUserId(userId);
                    newDoc.setAddresses(new ArrayList<>());
                    return newDoc;
                });

        // If first address, set as default
        if (userAddresses.getAddresses().isEmpty()) {
            newAddress.setDefault(true);
        }
        // If new address is default, unset others
        else if (newAddress.isDefault()) {
            userAddresses.getAddresses().forEach(addr -> addr.setDefault(false));
        }

        userAddresses.getAddresses().add(newAddress);
        return repository.save(userAddresses);
    }

    @Transactional
    public UserAddresses updateAddress(String userId, String addressId, UserAddresses.Address updatedAddress) {
        UserAddresses userAddresses = getUserAddresses(userId);

        UserAddresses.Address existingAddress = userAddresses.getAddresses().stream()
                .filter(addr -> addressId.equals(addr.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with ID: " + addressId + " for user: " + userId));

        // Handle default address logic
        if (updatedAddress.isDefault() && !existingAddress.isDefault()) {
            // Setting this address as default - unset all others
            userAddresses.getAddresses().forEach(addr -> addr.setDefault(false));
        } else if (!updatedAddress.isDefault() && existingAddress.isDefault()) {
            // Unsetting the default address - ensure we have another default
            if (userAddresses.getAddresses().stream().noneMatch(addr -> 
                !addressId.equals(addr.getId()) && addr.isDefault())) {
                throw new IllegalArgumentException(
                        "Cannot remove the last default address. Set another address as default first.");
            }
        }

        // Update address fields
        existingAddress.setStreet(updatedAddress.getStreet());
        existingAddress.setDistrict(updatedAddress.getDistrict());
        existingAddress.setState(updatedAddress.getState());
        existingAddress.setPincode(updatedAddress.getPincode());
        existingAddress.setLandmark(updatedAddress.getLandmark());
        existingAddress.setDefault(updatedAddress.isDefault());

        return repository.save(userAddresses);
    }

    public void deleteAddress(String userId, String addressId) {
        UserAddresses userAddresses = getUserAddresses(userId);
        
        Optional<UserAddresses.Address> addressToDelete = userAddresses.getAddresses().stream()
                .filter(addr -> addressId.equals(addr.getId()))
                .findFirst();
                
        if (addressToDelete.isEmpty()) {
            throw new ResourceNotFoundException("Address not found with ID: " + addressId);
        }
        
        boolean wasDefault = addressToDelete.get().isDefault();
        userAddresses.getAddresses().removeIf(addr -> addr.getId().equals(addressId));
        
        if (wasDefault && !userAddresses.getAddresses().isEmpty()) {
            // If we deleted the default address, set the first remaining as default
            userAddresses.getAddresses().get(0).setDefault(true);
        }
        
        if (userAddresses.getAddresses().isEmpty()) {
            repository.delete(userAddresses);
        } else {
            repository.save(userAddresses);
        }
    }

    public void deleteAllAddresses(String userId) {
        repository.deleteByUserId(userId);
    }

    // @Transactional
    // public UserAddresses toggleDefaultAddress(String userId, String addressId) {
    //     UserAddresses userAddresses = getUserAddresses(userId);
        
    //     UserAddresses.Address targetAddress = userAddresses.getAddresses().stream()
    //             .filter(addr -> addressId.equals(addr.getId()))
    //             .findFirst()
    //             .orElseThrow(() -> new ResourceNotFoundException(
    //                 "Address not found with ID: " + addressId));
        
    //     if (targetAddress.isDefault()) {
    //         if (userAddresses.getAddresses().size() == 1) {
    //             throw new IllegalStateException("Cannot unset default - user must have at least one default address");
    //         }
            
    //         // Find another address to make default
    //         UserAddresses.Address newDefault = userAddresses.getAddresses().stream()
    //                 .filter(addr -> !addressId.equals(addr.getId()))
    //                 .findFirst()
    //                 .orElseThrow();
            
    //         targetAddress.setDefault(false);
    //         newDefault.setDefault(true);
    //     } else {
    //         // Make this address the only default
    //         userAddresses.getAddresses().forEach(addr -> 
    //             addr.setDefault(addr.getId().equals(addressId)));
    //     }
        
    //     return repository.save(userAddresses);
    // }

    @Transactional
public UserAddresses toggleDefaultAddress(String userId, String addressId) {
    UserAddresses userAddresses = getUserAddresses(userId);
    
    UserAddresses.Address targetAddress = userAddresses.getAddresses().stream()
            .filter(addr -> addressId.equals(addr.getId()))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(
                "Address not found with ID: " + addressId));
    
    if (targetAddress.isDefault()) {
        if (userAddresses.getAddresses().size() == 1) {
            throw new IllegalStateException("Cannot unset default - user must have at least one default address");
        }
        
        // Find another address to make default
        UserAddresses.Address newDefault = userAddresses.getAddresses().stream()
                .filter(addr -> !addressId.equals(addr.getId()))
                .findFirst()
                .orElseThrow();
        
        targetAddress.setDefault(false);
        newDefault.setDefault(true);
    } else {
        // Make this address the only default
        userAddresses.getAddresses().forEach(addr -> 
            addr.setDefault(addr.getId().equals(addressId)));
    }
    
    return repository.save(userAddresses);
}

    

    public UserAddresses.Address getAddressById(String userId, String addressId) {
        UserAddresses userAddresses = getUserAddresses(userId);

        return userAddresses.getAddresses().stream()
                .filter(addr -> addressId.equals(addr.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with ID: " + addressId + " for user: " + userId));
    }

    private void validateDefaultAddress(List<UserAddresses.Address> addresses) {
        if (addresses.stream().filter(UserAddresses.Address::isDefault).count() != 1) {
            throw new IllegalArgumentException("Exactly one address must be set as default");
        }
    }

    private void validateAddressList(List<UserAddresses.Address> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            throw new IllegalArgumentException("Address list cannot be null or empty");
        }
    }
}

