//package com.recnaile.bulkorderservice.config;
//
//import com.recnaile.bulkorderservice.model.AdminUser;
//import com.recnaile.bulkorderservice.repository.AdminUserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final AdminUserRepository adminUserRepository;
//
//    @Autowired
//    @Qualifier("adminMongoTemplate") // Specify which MongoTemplate to use
//    private MongoTemplate mongoTemplate;
//
//    public SecurityConfig(AdminUserRepository adminUserRepository) {
//        this.adminUserRepository = adminUserRepository;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/bulk-orders").permitAll()
//                        .requestMatchers("/api/bulk-orders/**").authenticated()
//                        .anyRequest().authenticated()
//                )
//                .httpBasic()
//                .and()
//                .csrf().disable();
//
//        return http.build();
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return username -> adminUserRepository.findByUsername(username)
//                .map(adminUser -> User.builder()
//                        .username(adminUser.getUsername())
//                        .password(adminUser.getPassword())
//                        .roles("ADMIN")
//                        .accountExpired(!adminUser.isAccountNonExpired())
//                        .accountLocked(!adminUser.isAccountNonLocked())
//                        .credentialsExpired(!adminUser.isCredentialsNonExpired())
//                        .disabled(!adminUser.isEnabled())
//                        .build())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}


package com.recnaile.bulkorderservice.config;

import com.recnaile.bulkorderservice.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AdminUserRepository adminUserRepository;

    @Autowired
    public SecurityConfig(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/bulk-orders").permitAll() // Only POST is public
                        .requestMatchers("/api/bulk-orders/**").hasRole("ADMIN") // All other endpoints need ADMIN
                        .anyRequest().authenticated()
                )
                .httpBasic()
                .and()
                .csrf().disable();

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> adminUserRepository.findByUsername(username)
                .map(adminUser -> User.builder()
                        .username(adminUser.getUsername())
                        .password(adminUser.getPassword())
                        .roles("ADMIN")
                        .accountExpired(!adminUser.isAccountNonExpired())
                        .accountLocked(!adminUser.isAccountNonLocked())
                        .credentialsExpired(!adminUser.isCredentialsNonExpired())
                        .disabled(!adminUser.isEnabled())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}