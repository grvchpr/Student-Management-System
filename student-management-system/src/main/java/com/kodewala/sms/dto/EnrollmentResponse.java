package com.kodewala.sms.dto;

import com.kodewala.sms.entity.EnrollmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Enrollment response")
public class EnrollmentResponse {

    private Long id;

    @Schema(example = "1")
    private Long studentId;

    @Schema(example = "Gourav Chopra")
    private String studentName;

    @Schema(example = "1")
    private Long courseId;

    @Schema(example = "Java Full Stack Development")
    private String courseName;

    @Schema(example = "JAVA101")
    private String courseCode;

    @Schema(example = "2026-09-23")
    private LocalDate enrollmentDate;

    @Schema(example = "ACTIVE")
    private EnrollmentStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}