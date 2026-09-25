package com.kodewala.sms.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kodewala.sms.dto.UserResponse;
import com.kodewala.sms.service.UserRegistrationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserRegistrationService userRegistrationService;

    @GetMapping("/pending")
    public List<UserResponse> getPendingUsers() {
        return userRegistrationService.getPendingUsers();
    }

    @PutMapping("/{id}/approve")
    public UserResponse approve(@PathVariable Long id) {
        return userRegistrationService.approve(id);
    }

    @PutMapping("/{id}/reject")
    public UserResponse reject(@PathVariable Long id) {
        return userRegistrationService.reject(id);
    }
}
