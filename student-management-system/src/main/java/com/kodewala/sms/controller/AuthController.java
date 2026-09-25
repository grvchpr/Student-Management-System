package com.kodewala.sms.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kodewala.sms.dto.ChangePasswordRequest;
import com.kodewala.sms.dto.LoginRequest;
import com.kodewala.sms.dto.LoginResponse;
import com.kodewala.sms.dto.RegisterRequest;
import com.kodewala.sms.dto.UserResponse;
import com.kodewala.sms.service.AuthService;
import com.kodewala.sms.service.UserRegistrationService;

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
    description = "Authentication and registration APIs"
)
public class AuthController {

    private final AuthService authService;
    private final UserRegistrationService userRegistrationService;

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Login")
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
            responseCode = "403",
            description = "Account is pending or rejected"
        )
    })
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

    @PostMapping("/register")
    @SecurityRequirements
    @Operation(
        summary = "Register user",
        description =
            "Create a pending USER account for administrator approval"
    )
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userRegistrationService.register(request));
    }

    @PostMapping("/change-password")
    @Operation(
        summary = "Change password",
        description =
            "Change the password of the currently authenticated user"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Password changed successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid password request"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required"
        )
    })
    public ResponseEntity<Void> changePassword(
            Principal principal,
            @Valid @RequestBody ChangePasswordRequest request) {

        authService.changePassword(
                principal.getName(),
                request
        );

        return ResponseEntity.ok().build();
    }
}