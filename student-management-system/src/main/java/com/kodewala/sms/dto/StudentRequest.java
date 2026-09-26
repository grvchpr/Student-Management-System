package com.kodewala.sms.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating or updating a student")
public class StudentRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Schema(example = "Gourav")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Schema(example = "Chopra")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(example = "gourav.chopra@gmail.com")
    private String email;

    @Pattern(
            regexp = "^[0-9]{10,15}$",
            message = "Phone must contain 10 to 15 digits"
    )
    @Schema(example = "9876543210")
    private String phone;

    @Past(message = "Date of birth must be in the past")
    @Schema(example = "1996-12-27")
    private LocalDate dateOfBirth;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    @Schema(example = "Bangalore")
    private String address;
}