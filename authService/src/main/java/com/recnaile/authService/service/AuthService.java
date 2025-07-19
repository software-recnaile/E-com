//package com.recnaile.authService.service;
//
//import com.recnaile.authService.dto.AuthRequest;
//import com.recnaile.authService.dto.AuthResponse;
//import com.recnaile.authService.exception.UserNotVerifiedException;
//import com.recnaile.authService.model.User;
//import com.recnaile.authService.repository.UserRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AuthService {
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private JwtService jwtService;
//
//    @Autowired
//    private UserRepository userRepository;
//    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
//
//    public AuthResponse authenticate(AuthRequest authRequest) {
//        logger.info("Attempting to authenticate user: {}", authRequest.getEmail());
//
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            authRequest.getEmail(),
//                            authRequest.getPassword()
//                    )
//            );
//
//            logger.info("Authentication result: {}", authentication.isAuthenticated());
//
//            if (authentication.isAuthenticated()) {
//                User user = userRepository.findByEmail(authRequest.getEmail())
//                        .orElseThrow(() -> {
//                            logger.error("User not found after authentication: {}", authRequest.getEmail());
//                            return new UsernameNotFoundException("User not found");
//                        });
//
//                logger.info("User enabled status: {}", user.isEnabled());
//
//                if (!user.isEnabled()) {
//                    logger.warn("User not verified: {}", authRequest.getEmail());
//                    throw new UserNotVerifiedException("User email is not verified");
//                }
//
//                String token = jwtService.generateToken(authRequest.getEmail());
//
//                AuthResponse response = new AuthResponse();
//                response.setToken(token);
//                response.setEmail(user.getEmail());
//                response.setUsername(user.getUsername());
//
//                logger.info("Successfully authenticated user: {}", authRequest.getEmail());
//
//                return response;
//            } else {
//                logger.error("Authentication failed for user: {}", authRequest.getEmail());
//                throw new UsernameNotFoundException("Invalid user request");
//            }
//        } catch (BadCredentialsException e) {
//            logger.error("Bad credentials for user: {}", authRequest.getEmail());
//            throw new BadCredentialsException("Invalid username or password");
//        }
//    }
//}

package com.recnaile.authService.service;

import com.recnaile.authService.dto.AuthRequest;
import com.recnaile.authService.dto.AuthResponse;
import com.recnaile.authService.exception.UserNotVerifiedException;
import com.recnaile.authService.model.User;
import com.recnaile.authService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    public AuthResponse authenticate(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (!user.isEnabled()) {
                throw new UserNotVerifiedException("User email is not verified");
            }

            String token = jwtService.generateToken(authRequest.getEmail());

            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setEmail(user.getEmail());
            response.setUsername(user.getUsername());

            return response;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }
}