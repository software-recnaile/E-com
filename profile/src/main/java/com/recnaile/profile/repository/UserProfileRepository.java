package com.recnaile.profile.repository;

import com.recnaile.profile.model.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    Optional<UserProfile> findByUserId(String userId);
    boolean existsByUserId(String userId);

    @Query(value = "{'userId': ?0}", delete = true)
    void deleteByUserId(String userId);

    long countByUserId(String userId); // Useful for checking duplicates
}