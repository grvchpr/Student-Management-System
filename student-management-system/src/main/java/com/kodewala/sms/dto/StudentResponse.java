package com.kodewala.sms.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Student response object")
public class StudentResponse {

    @Schema(
            description = "Unique student ID",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Student first name",
            example = "Gourav"
    )
    private String firstName;

    @Schema(
            description = "Student last name",
            example = "Chopra"
    )
    private String lastName;

    @Schema(
            description = "Student email",
            example = "gourav.chopra@gmail.com"
    )
    private String email;

    @Schema(
            description = "Student phone number",
            example = "9876543210"
    )
    private String phone;

    @Schema(
            description = "Student date of birth",
            example = "1996-12-27"
    )
    private LocalDate dateOfBirth;

    @Schema(
            description = "Student address",
            example = "Bangalore"
    )
    private String address;

    @Schema(
            description = "Student creation timestamp",
            example = "2026-09-23T12:30:00"
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "Student last update timestamp",
            example = "2026-09-23T12:30:00"
    )
    private LocalDateTime updatedAt;
}