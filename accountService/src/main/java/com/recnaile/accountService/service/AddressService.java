package com.recnaile.accountService.service;

import com.recnaile.accountService.exception.ResourceNotFoundException;
import com.recnaile.accountService.model.UserAddresses;
import com.recnaile.accountService.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository repository;
    private final MongoTemplate mongoTemplate;

    // ✅ Validate one address ID
    public boolean verifyAddressBelongsToUser(String userId, String addressId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                .and("addresses._id").is(addressId));
        return mongoTemplate.exists(query, "user_addresses");
    }

    // ✅ Validate both shipping and billing addresses
    public void validateShippingAndBillingAddressIds(String userId, String shippingAddressId, String billingAddressId) {
        boolean shippingValid = verifyAddressBelongsToUser(userId, shippingAddressId);
        boolean billingValid = verifyAddressBelongsToUser(userId, billingAddressId);

        if (!shippingValid) {
            throw new ResourceNotFoundException("Shipping address not found or doesn't belong to user");
        }
        if (!billingValid) {
            throw new ResourceNotFoundException("Billing address not found or doesn't belong to user");
        }
    }

    public UserAddresses getUserAddresses(String userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No addresses found for user"));
    }

    public UserAddresses.Address getAddressById(String userId, String addressId) {
        if (!verifyAddressBelongsToUser(userId, addressId)) {
            throw new ResourceNotFoundException("Address not found or doesn't belong to user");
        }

        UserAddresses userAddresses = getUserAddresses(userId);
        return userAddresses.getAddresses().stream()
                .filter(addr -> addr.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

    public UserAddresses.Address getDefaultAddress(String userId) {
        UserAddresses userAddresses = getUserAddresses(userId);
        return userAddresses.getAddresses().stream()
                .filter(UserAddresses.Address::isDefault)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No default address found"));
    }

    public boolean hasDefaultAddress(String userId) {
        try {
            UserAddresses userAddresses = getUserAddresses(userId);
            return userAddresses.getAddresses().stream()
                    .anyMatch(UserAddresses.Address::isDefault);
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public UserAddresses createOrUpdateAddresses(String userId, List<UserAddresses.Address> addressList) {
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
        UserAddresses userAddresses = repository.findByUserId(userId)
                .orElseGet(() -> {
                    UserAddresses newDoc = new UserAddresses();
                    newDoc.setUserId(userId);
                    return newDoc;
                });

        if (newAddress.isDefault()) {
            userAddresses.getAddresses().forEach(addr -> addr.setDefault(false));
        }

        userAddresses.getAddresses().add(newAddress);
        return repository.save(userAddresses);
    }

    public UserAddresses updateAddress(String userId, String addressId, UserAddresses.Address updatedAddress) {
        if (!verifyAddressBelongsToUser(userId, addressId)) {
            throw new ResourceNotFoundException("Address not found or doesn't belong to user");
        }

        UserAddresses userAddresses = getUserAddresses(userId);

        if (updatedAddress.isDefault()) {
            userAddresses.getAddresses().forEach(addr -> addr.setDefault(false));
        }

        userAddresses.getAddresses().stream()
                .filter(addr -> addr.getId().equals(addressId))
                .findFirst()
                .ifPresent(addr -> {
                    addr.setStreet(updatedAddress.getStreet());
                    addr.setDistrict(updatedAddress.getDistrict());
                    addr.setState(updatedAddress.getState());
                    addr.setPincode(updatedAddress.getPincode());
                    addr.setLandmark(updatedAddress.getLandmark());
                    addr.setDefault(updatedAddress.isDefault());
                });

        return repository.save(userAddresses);
    }

    public void deleteAddress(String userId, String addressId) {
        if (!verifyAddressBelongsToUser(userId, addressId)) {
            throw new ResourceNotFoundException("Address not found or doesn't belong to user");
        }

        UserAddresses userAddresses = getUserAddresses(userId);
        userAddresses.getAddresses().removeIf(addr -> addr.getId().equals(addressId));

        if (userAddresses.getAddresses().isEmpty()) {
            repository.delete(userAddresses);
        } else {
            repository.save(userAddresses);
        }
    }

    public void deleteAllAddresses(String userId) {
        repository.deleteByUserId(userId);
    }

    private void validateDefaultAddress(List<UserAddresses.Address> addresses) {
        long defaultCount = addresses.stream()
                .filter(UserAddresses.Address::isDefault)
                .count();

        if (defaultCount > 1) {
            throw new IllegalArgumentException("Only one address can be set as default");
        }
    }
}
