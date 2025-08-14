//package com.recnaile.accountService.repository;
//
//import com.recnaile.accountService.model.Address;
//import org.springframework.data.mongodb.repository.MongoRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface AddressRepository extends MongoRepository<Address, String> {
//    List<Address> findByUserId(String userId);
//    Optional<Address> findByUserIdAndIsDefault(String userId, boolean isDefault);
//    Optional<Address> findByIdAndUserId(String id, String userId);
//    void deleteByIdAndUserId(String id, String userId);
//}

package com.recnaile.accountService.repository;

import com.recnaile.accountService.model.UserAddresses;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AddressRepository extends MongoRepository<UserAddresses, String> {
    Optional<UserAddresses> findByUserId(String userId);
    void deleteByUserId(String userId);

}