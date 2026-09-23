package com.kodewala.sms.dto;

import com.kodewala.sms.entity.EnrollmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request for enrolling a student into a course")
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

    @Schema(
            description = "Enrollment date",
            example = "2026-09-23"
    )
    private LocalDate enrollmentDate;

    @Schema(
            description = "Enrollment status",
            example = "ACTIVE"
    )
    private EnrollmentStatus status;
}