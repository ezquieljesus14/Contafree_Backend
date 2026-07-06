package com.contafree.auth_service.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.contafree.auth_service.entity.User;
import com.contafree.auth_service.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedTestUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("test@contafree.com").isEmpty()) {
                User user = new User();
                user.setEmail("test@contafree.com");
                user.setPasswordHash(passwordEncoder.encode("Test1234!"));
                user.setRoles(Set.of("ROLE_USER"));
                userRepository.save(user);
                System.out.println(">>> Test user created: test@contafree.com / Test1234!");
            } 
            if (userRepository.findByEmail("testAdmin@contafree.com").isEmpty()) {
            	User user = new User();
                user.setEmail("testAdmin@contafree.com");
                user.setPasswordHash(passwordEncoder.encode("Admin1234!Cf"));
                user.setRoles(Set.of("ROLE_ADMIN"));
                userRepository.save(user);
                System.out.println(">>> Test Admin created: testAdmin@contafree.com / Admin1234!Cf");
			}
        };
    }
}