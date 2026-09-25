package com.kodewala.sms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Login request")
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Schema(
        description = "Username used for authentication",
        example = "admin"
    )
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(
        description = "User password",
        example = "Admin@123"
    )
    private String password;
}