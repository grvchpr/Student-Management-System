package com.kodewala.sms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Successful login response")
public class LoginResponse {

    @Schema(
        description = "JWT authentication token",
        example = "eyJhbGciOiJIUzI1NiJ9..."
    )
    private String token;

    @Schema(
        description = "Token type",
        example = "Bearer"
    )
    private String tokenType;
}