package com.recnaile.profile.service;

import com.recnaile.profile.exception.DuplicateResourceException;
import com.recnaile.profile.exception.ResourceNotFoundException;
import com.recnaile.profile.model.UserProfile;
import com.recnaile.profile.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository repository;

    public UserProfile createProfile(UserProfile profile) {
        // More strict duplicate check
        if (repository.countByUserId(profile.getUserId()) > 0) {
            throw new DuplicateResourceException("User profile already exists for user ID: " + profile.getUserId());
        }

        profile.setCreatedDate(LocalDateTime.now());
        profile.setLastModifiedDate(LocalDateTime.now());

        try {
            return repository.save(profile);
        } catch (DuplicateKeyException e) {
            throw new DuplicateResourceException("User profile already exists for user ID: " + profile.getUserId());
        }
    }

    public UserProfile getProfileByUserId(String userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user ID: " + userId));
    }

    public UserProfile updateProfile(String userId, UserProfile updatedProfile) {
        UserProfile existing = getProfileByUserId(userId);

        if (!userId.equals(updatedProfile.getUserId())) {
            throw new IllegalArgumentException("Cannot change user ID");
        }

        updatedProfile.setId(existing.getId());
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
}