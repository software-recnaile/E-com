package com.recnaile.bulkorderservice.repository;

import com.recnaile.bulkorderservice.model.AdminUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

public interface AdminUserRepository extends MongoRepository<AdminUser, String> {
    @Query(value = "{'username' : ?0}", fields = "{}")
    Optional<AdminUser> findByUsername(String username);
}