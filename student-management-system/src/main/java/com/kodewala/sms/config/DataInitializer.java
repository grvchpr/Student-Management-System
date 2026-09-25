package com.kodewala.sms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.kodewala.sms.entity.User;
import com.kodewala.sms.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:}")
    private String adminUsername;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Value("${app.user.username:}")
    private String userUsername;

    @Value("${app.user.password:}")
    private String userPassword;

    @Override
    public void run(String... args) {
        createAdminUser();
        createNormalUser();
    }

    private void createAdminUser() {
        if (adminUsername == null || adminUsername.isBlank()) {
            throw new IllegalStateException("APP_ADMIN_USERNAME is not configured");
        }
        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException("APP_ADMIN_PASSWORD is not configured");
        }
        if (userRepository.existsByUsername(adminUsername)) {
            return;
        }

        User admin = User.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .role("ADMIN")
                .status("APPROVED")
                .build();

        userRepository.save(admin);
        System.out.println("Initial admin user created: " + adminUsername);
    }

    private void createNormalUser() {
        if (userUsername == null || userUsername.isBlank()) {
            throw new IllegalStateException("APP_USER_USERNAME is not configured");
        }
        if (userPassword == null || userPassword.isBlank()) {
            throw new IllegalStateException("APP_USER_PASSWORD is not configured");
        }
        if (userRepository.existsByUsername(userUsername)) {
            return;
        }

        User user = User.builder()
                .username(userUsername)
                .password(passwordEncoder.encode(userPassword))
                .role("USER")
                .status("APPROVED")
                .build();

        userRepository.save(user);
        System.out.println("Initial user created: " + userUsername);
    }
}
