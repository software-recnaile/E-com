////package com.recnaile.accountService.service;
////
////import com.recnaile.accountService.exception.ResourceNotFoundException;
////import com.recnaile.accountService.model.Address;
////import com.recnaile.accountService.repository.AddressRepository;
////import lombok.RequiredArgsConstructor;
////import org.springframework.stereotype.Service;
////
////import java.util.List;
////
////@Service
////@RequiredArgsConstructor
////public class AddressService {
////    private final AddressRepository repository;
////
////    public Address addAddress(Address address) {
////        if (address.isDefault()) {
////            // Reset any existing default address
////            repository.findByUserIdAndIsDefault(address.getUserId(), true)
////                    .ifPresent(addr -> {
////                        addr.setDefault(false);
////                        repository.save(addr);
////                    });
////        }
////        return repository.save(address);
////    }
////
////    public List<Address> getUserAddresses(String userId) {
////        return repository.findByUserId(userId);
////    }
////
////    public Address getAddress(String id, String userId) {
////        return repository.findByIdAndUserId(id, userId)
////                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
////    }
////
////    public Address updateAddress(String id, String userId, Address updatedAddress) {
////        Address existing = getAddress(id, userId);
////
////        if (updatedAddress.isDefault() && !existing.isDefault()) {
////            // Reset any existing default address
////            repository.findByUserIdAndIsDefault(userId, true)
////                    .ifPresent(addr -> {
////                        addr.setDefault(false);
////                        repository.save(addr);
////                    });
////        }
////
////        existing.setStreet(updatedAddress.getStreet());
////        existing.setDistrict(updatedAddress.getDistrict());
////        existing.setState(updatedAddress.getState());
////        existing.setPincode(updatedAddress.getPincode());
////        existing.setLandmark(updatedAddress.getLandmark());
////        existing.setDefault(updatedAddress.isDefault());
////
////        return repository.save(existing);
////    }
////
////    public void deleteAddress(String id, String userId) {
////        repository.deleteByIdAndUserId(id, userId);
////    }
////
////    public Address setDefaultAddress(String id, String userId) {
////        // Reset any existing default address
////        repository.findByUserIdAndIsDefault(userId, true)
////                .ifPresent(addr -> {
////                    addr.setDefault(false);
////                    repository.save(addr);
////                });
////
////        Address address = getAddress(id, userId);
////        address.setDefault(true);
////        return repository.save(address);
////    }
////}
//
//package com.recnaile.accountService.service;
//
//import com.recnaile.accountService.exception.ResourceNotFoundException;
//import com.recnaile.accountService.model.UserAddresses;
//import com.recnaile.accountService.repository.AddressRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class AddressService {
//    private final AddressRepository repository;
//
//    public UserAddresses createOrUpdateAddresses(String userId, List<UserAddresses.Address> addressList) {
//        // Validate only one default address
//        validateDefaultAddress(addressList);
//
//        // Check if user already has addresses
//        UserAddresses existing = repository.findByUserId(userId).orElse(null);
//
//
//
//        if (existing != null) {
//            existing.setAddresses(addressList);
//            return repository.save(existing);
//        } else {
//            UserAddresses newAddresses = new UserAddresses();
//            newAddresses.setUserId(userId);
//            newAddresses.setAddresses(addressList);
//            return repository.save(newAddresses);
//        }
//    }
//
//    public UserAddresses getUserAddresses(String userId) {
//        return repository.findByUserId(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("No addresses found for user"));
//    }
//
//
//
//    public UserAddresses addAddress(String userId, UserAddresses.Address newAddress) {
//        UserAddresses userAddresses = repository.findByUserId(userId)
//                .orElseGet(() -> {
//                    UserAddresses newDoc = new UserAddresses();
//                    newDoc.setUserId(userId);
//                    return newDoc;
//                });
//
//        if (newAddress.isDefault()) {
//            // Reset any existing default address
//            userAddresses.getAddresses().forEach(addr -> addr.setDefault(false));
//        }
//
//        userAddresses.getAddresses().add(newAddress);
//        return repository.save(userAddresses);
//    }
//
//
//    public UserAddresses updateAddress(String userId, String addressIndex, UserAddresses.Address updatedAddress) {
//        UserAddresses userAddresses = getUserAddresses(userId);
//        int index = Integer.parseInt(addressIndex);
//
//        if (index < 0 || index >= userAddresses.getAddresses().size()) {
//            throw new ResourceNotFoundException("Address not found at index: " + index);
//        }
//
//        if (updatedAddress.isDefault()) {
//            // Reset any existing default address
//            userAddresses.getAddresses().forEach(addr -> addr.setDefault(false));
//        }
//
//        userAddresses.getAddresses().set(index, updatedAddress);
//        return repository.save(userAddresses);
//    }
//
//    public void deleteAddress(String userId, String addressIndex) {
//        UserAddresses userAddresses = getUserAddresses(userId);
//        int index = Integer.parseInt(addressIndex);
//
//        if (index < 0 || index >= userAddresses.getAddresses().size()) {
//            throw new ResourceNotFoundException("Address not found at index: " + index);
//        }
//
//        userAddresses.getAddresses().remove(index);
//
//        if (userAddresses.getAddresses().isEmpty()) {
//            repository.delete(userAddresses);
//        } else {
//            repository.save(userAddresses);
//        }
//    }
//
//    public void deleteAllAddresses(String userId) {
//        repository.deleteByUserId(userId);
//    }
//
//    private void validateDefaultAddress(List<UserAddresses.Address> addresses) {
//        long defaultCount = addresses.stream()
//                .filter(UserAddresses.Address::isDefault)
//                .count();
//
//        if (defaultCount > 1) {
//            throw new IllegalArgumentException("Only one address can be set as default");
//        }
//    }
//}

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
public class AddressService {
    private final AddressRepository repository;

    public UserAddresses getUserAddresses(String userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No addresses found for user"));
    }

    public UserAddresses.Address getAddressById(String userId, String addressId) {
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

    public boolean verifyAddressBelongsToUser(String userId, String addressId) {
        try {
            getAddressById(userId, addressId);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
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
        // Validate only one default address
        validateDefaultAddress(addressList);

        // Check if user already has addresses
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
            // Reset any existing default address
            userAddresses.getAddresses().forEach(addr -> addr.setDefault(false));
        }

        userAddresses.getAddresses().add(newAddress);
        return repository.save(userAddresses);
    }


    public UserAddresses updateAddress(String userId, String addressIndex, UserAddresses.Address updatedAddress) {
        UserAddresses userAddresses = getUserAddresses(userId);
        int index = Integer.parseInt(addressIndex);

        if (index < 0 || index >= userAddresses.getAddresses().size()) {
            throw new ResourceNotFoundException("Address not found at index: " + index);
        }

        if (updatedAddress.isDefault()) {
            // Reset any existing default address
            userAddresses.getAddresses().forEach(addr -> addr.setDefault(false));
        }

        userAddresses.getAddresses().set(index, updatedAddress);
        return repository.save(userAddresses);
    }

    public void deleteAddress(String userId, String addressIndex) {
        UserAddresses userAddresses = getUserAddresses(userId);
        int index = Integer.parseInt(addressIndex);

        if (index < 0 || index >= userAddresses.getAddresses().size()) {
            throw new ResourceNotFoundException("Address not found at index: " + index);
        }

        userAddresses.getAddresses().remove(index);

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