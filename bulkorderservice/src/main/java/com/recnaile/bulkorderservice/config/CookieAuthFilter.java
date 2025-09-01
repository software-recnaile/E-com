package com.recnaile.bulkorderservice.config;

import com.recnaile.bulkorderservice.model.AdminUser;
import com.recnaile.bulkorderservice.repository.AdminUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

public class CookieAuthFilter extends OncePerRequestFilter {

    private final AdminUserRepository adminUserRepository;

    public CookieAuthFilter(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip filter for public endpoints
        if (request.getRequestURI().equals("/api/bulk-orders") && "POST".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract userId from cookie
        String userId = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("userId".equals(cookie.getName())) {
                    userId = cookie.getValue();
                    break;
                }
            }
        }

        if (userId != null) {
            // Check if user exists and is admin
            Optional<AdminUser> user = adminUserRepository.findById(userId);
            if (user.isPresent() && user.get().isEnabled()) {
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        user.get().getUsername(),
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}