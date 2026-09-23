package com.kodewala.sms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Course response object")
public class CourseResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "Java Full Stack Development")
    private String courseName;

    @Schema(example = "JAVA101")
    private String courseCode;

    @Schema(example = "6 Months")
    private String duration;

    @Schema(example = "45000.00")
    private BigDecimal fees;

    @Schema(example = "Java, Spring Boot, Microservices and SQL")
    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}