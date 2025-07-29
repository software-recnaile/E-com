package com.recnaile.address.repository;

import com.recnaile.address.model.UserAddresses;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AddressRepository extends MongoRepository<UserAddresses, String> {
    Optional<UserAddresses> findByUserId(String userId);
    void deleteByUserId(String userId);
}
