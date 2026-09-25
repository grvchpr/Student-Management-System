package com.kodewala.sms.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kodewala.sms.dto.ChangePasswordRequest;
import com.kodewala.sms.entity.User;
import com.kodewala.sms.exception.AccountPendingException;
import com.kodewala.sms.exception.AccountRejectedException;
import com.kodewala.sms.repository.UserRepository;
import com.kodewala.sms.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String authenticateAndGenerateToken(
            String username,
            String password) {

        User user = userRepository
                .findByUsername(username.trim())
                .orElse(null);

        if (user != null) {

            if ("PENDING".equalsIgnoreCase(user.getStatus())) {
                throw new AccountPendingException(
                        "Your account is pending administrator approval."
                );
            }

            if ("REJECTED".equalsIgnoreCase(user.getStatus())) {
                throw new AccountRejectedException(
                        "Your account registration was rejected."
                );
            }
        }

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                username.trim(),
                                password
                        )
                );

        return jwtService.generateToken(
                authentication.getPrincipal()
                        instanceof org.springframework.security.core.userdetails.UserDetails userDetails
                        ? userDetails
                        : (org.springframework.security.core.userdetails.UserDetails)
                                authentication.getPrincipal()
        );
    }

    @Transactional
    public void changePassword(
            String username,
            ChangePasswordRequest request) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User account not found"
                        )
                );

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "Current password is incorrect"
            );
        }

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new IllegalArgumentException(
                    "New password and confirm password do not match"
            );
        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "New password must be different from current password"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);
    }
}