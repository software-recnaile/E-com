//package com.recnaile.authService.service;
//
//import com.recnaile.authService.dto.SignUpRequest;
//import com.recnaile.authService.exception.UserAlreadyExistsException;
//import com.recnaile.authService.model.User;
//import com.recnaile.authService.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//
//@Service
//public class UserService {
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private OtpService otpService;
//
//    public User registerUser(SignUpRequest signUpRequest) {
//        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//            throw new UserAlreadyExistsException("Email already in use");
//        }
//
//        User user = new User();
//        user.setUsername(signUpRequest.getUsername());
//        user.setEmail(signUpRequest.getEmail());
//        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
//        user.setEnabled(false);
//        user.setCreatedDate(new Date());
//        user.setLastModifiedDate(new Date());
//
//        User savedUser = userRepository.save(user);
//
//        // Send OTP for verification
//        otpService.sendOtpEmail(user.getEmail());
//
////        return savedUser;
//        return userRepository.save(user);
//    }
//
//    public void verifyUser(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        user.setEnabled(true);
//        userRepository.save(user); // Make sure this is saving properly
//    }
//
//    public void initiatePasswordReset(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        otpService.sendOtpEmail(email);
//    }
//
//    public void resetPassword(String email, String newPassword) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        user.setPassword(passwordEncoder.encode(newPassword));
//        userRepository.save(user);
//    }
//    public void verifyPasswordEncoding(String email, String rawPassword) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        System.out.println("Stored encoded password: " + user.getPassword());
//        System.out.println("Matches input password: " +
//                passwordEncoder.matches(rawPassword, user.getPassword()));
//    }
//}


package com.recnaile.authService.service;

import com.recnaile.authService.dto.SignUpRequest;
import com.recnaile.authService.dto.UserDisplayDto;
import com.recnaile.authService.exception.UserAlreadyExistsException;
import com.recnaile.authService.model.User;
import com.recnaile.authService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private LoggingService loggingService;

    public User registerUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setEnabled(false);
        user.setCreatedDate(new Date());
        user.setLastModifiedDate(new Date());

        User savedUser = userRepository.save(user);

        // Send OTP for verification
        otpService.sendOtpEmail(user.getEmail(), "verification");

        loggingService.logSignUp(user.getEmail(), user.getUsername());

        return savedUser;
    }

    public void verifyUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void initiatePasswordReset(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new RuntimeException("User not found");
        }
        otpService.sendOtpEmail(email, "password_reset");
    }

    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastModifiedDate(new Date());
        userRepository.save(user);
    }
    public List<UserDisplayDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserDisplayDto dto = new UserDisplayDto();
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    dto.setAdmin(user.isAdmin());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    public List<UserDisplayDto> searchUsers(String email, Boolean isAdmin) {
        Query query = new Query();

        if (email != null) {
            query.addCriteria(Criteria.where("email").regex(email, "i"));
        }

        if (isAdmin != null) {
            query.addCriteria(Criteria.where("isAdmin").is(isAdmin));
        }

        return mongoTemplate.find(query, User.class).stream()
                .map(user -> {
                    UserDisplayDto dto = new UserDisplayDto();
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    dto.setAdmin(user.isAdmin());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    public void updateAdminStatus(String email, boolean isAdmin) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAdmin(isAdmin);
        userRepository.save(user);
        loggingService.logAdminChange(email, user.getUsername(), isAdmin);
    }
}