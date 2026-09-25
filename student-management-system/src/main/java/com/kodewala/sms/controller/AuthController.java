package com.kodewala.sms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kodewala.sms.dto.LoginRequest;
import com.kodewala.sms.dto.LoginResponse;
import com.kodewala.sms.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
    name = "Authentication",
    description = "Authentication and JWT token management APIs"
)
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
        summary = "Login",
        description = "Authenticate a user and generate a JWT token"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Login successful"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid username or password"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request"
        )
    })
    @SecurityRequirements
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.authenticateAndGenerateToken(
                request.getUsername(),
                request.getPassword()
        );

        return ResponseEntity.ok(
                new LoginResponse(token, "Bearer")
        );
    }
}