package com.kodewala.sms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for creating or updating a course")
public class CourseRequest {

    @NotBlank(message = "Course name is required")
    @Size(
            max = 100,
            message = "Course name must not exceed 100 characters"
    )
    @Schema(
            description = "Name of the course",
            example = "Java Full Stack Development"
    )
    private String courseName;

    @NotBlank(message = "Course code is required")
    @Size(
            max = 20,
            message = "Course code must not exceed 20 characters"
    )
    @Schema(
            description = "Unique course code",
            example = "JAVA101"
    )
    private String courseCode;

    @NotBlank(message = "Duration is required")
    @Schema(
            description = "Course duration",
            example = "6 Months"
    )
    private String duration;

    @NotNull(message = "Fees are required")
    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Fees cannot be negative"
    )
    @Schema(
            description = "Course fees",
            example = "45000.00"
    )
    private BigDecimal fees;

    @Size(
            max = 500,
            message = "Description must not exceed 500 characters"
    )
    @Schema(
            description = "Course description",
            example = "Java, Spring Boot, Microservices and SQL"
    )
    private String description;
}