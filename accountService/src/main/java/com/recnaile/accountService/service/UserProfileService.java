package com.recnaile.accountService.service;

import com.recnaile.accountService.exception.ResourceNotFoundException;
import com.recnaile.accountService.model.UserProfile;
import com.recnaile.accountService.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository repository;
    private final RestTemplate restTemplate;

    @Qualifier("accountDbTemplate")
    private final MongoTemplate accountDbTemplate;

    public UserProfile createProfile(UserProfile profile) {
        profile.setCreatedDate(LocalDateTime.now());
        profile.setLastModifiedDate(LocalDateTime.now());
        return repository.save(profile);
    }

    public UserProfile getProfileByUserId(String userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user ID: " + userId));
    }

    public UserProfile updateProfile(String userId, UserProfile updatedProfile) {
        UserProfile existing = getProfileByUserId(userId);

        updatedProfile.setId(existing.getId());
        updatedProfile.setUserId(userId);
        updatedProfile.setCreatedDate(existing.getCreatedDate());
        updatedProfile.setLastModifiedDate(LocalDateTime.now());

        return repository.save(updatedProfile);
    }

    public void deleteProfile(String userId) {
        if (!repository.existsByUserId(userId)) {
            throw new ResourceNotFoundException("Profile not found for user ID: " + userId);
        }
        repository.deleteByUserId(userId);
    }

    public List<UserProfile> getAllProfiles() {
        return repository.findAll();
    }

    public UserProfile addFavoriteProduct(String userId, String productId) {
        // This would use RestTemplate to fetch product details from product-db
        // and add to the favoriteProducts list
        // Implementation depends on your product service API
        return null; // Implement based on your needs
    }
}