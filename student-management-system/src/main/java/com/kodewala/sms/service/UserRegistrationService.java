package com.kodewala.sms.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kodewala.sms.dto.RegisterRequest;
import com.kodewala.sms.dto.UserResponse;
import com.kodewala.sms.entity.User;
import com.kodewala.sms.exception.DuplicateUsernameException;
import com.kodewala.sms.exception.UserNotFoundException;
import com.kodewala.sms.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException(
                    "Username already exists: " + username
            );
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .status("PENDING")
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getPendingUsers() {
        return userRepository.findByStatusOrderByCreatedAtAsc("PENDING")
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional
    public UserResponse approve(Long id) {
        User user = getUser(id);
        user.setStatus("APPROVED");
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse reject(Long id) {
        User user = getUser(id);
        user.setStatus("REJECTED");
        return UserResponse.from(userRepository.save(user));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found: " + id
                ));
    }
}
