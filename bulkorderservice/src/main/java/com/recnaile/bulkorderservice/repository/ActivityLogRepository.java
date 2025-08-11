//package com.recnaile.bulkorderservice.repository;
//
//import com.recnaile.bulkorderservice.model.ActivityLog;
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.data.mongodb.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface ActivityLogRepository extends MongoRepository<ActivityLog, String> {
//
//    @Query("{ 'entityType': ?0 }")
//    List<ActivityLog> findByEntityType(String entityType);
//
//    @Query("{ 'entityId': ?0 }")
//    List<ActivityLog> findByEntityId(String entityId);
//
//    @Query("{ 'username': ?0 }")
//    List<ActivityLog> findByUsername(String username);
//
//    @Query("{ 'entityType': ?0, 'entityId': ?1 }")
//    List<ActivityLog> findByEntityTypeAndEntityId(String entityType, String entityId);
//}