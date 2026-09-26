package com.kodewala.sms.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "Request payload for creating a student course enrollment"
)
public class EnrollmentRequest {

    @NotNull(message = "Student ID is required")
    @Schema(
            description = "Student ID",
            example = "1"
    )
    private Long studentId;

    @NotNull(message = "Course ID is required")
    @Schema(
            description = "Course ID",
            example = "1"
    )
    private Long courseId;

    @PastOrPresent(
            message = "Enrollment date cannot be in the future"
    )
    @Schema(
            description = "Enrollment date",
            example = "2026-09-25"
    )
    private LocalDate enrollmentDate;
}