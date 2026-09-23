package com.kodewala.sms.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kodewala.sms.dto.EnrollmentRequest;
import com.kodewala.sms.dto.EnrollmentResponse;
import com.kodewala.sms.entity.EnrollmentStatus;
import com.kodewala.sms.service.EnrollmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/enrollments")
@Tag(
        name = "Enrollment Management",
        description = "APIs for student course enrollment"
)
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(
            EnrollmentService enrollmentService) {

        this.enrollmentService = enrollmentService;
    }

    @Operation(
            summary = "Enroll student into course"
    )
    @PostMapping
    public ResponseEntity<EnrollmentResponse> enrollStudent(
            @Valid @RequestBody EnrollmentRequest request) {

        EnrollmentResponse response =
                enrollmentService.enrollStudent(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @Operation(
            summary = "Get enrollment by ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse>
    getEnrollmentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                enrollmentService.getEnrollmentById(id)
        );
    }

    @Operation(
            summary = "Get all enrollments"
    )
    @GetMapping
    public ResponseEntity<Page<EnrollmentResponse>>
    getAllEnrollments(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return ResponseEntity.ok(
                enrollmentService.getAllEnrollments(
                        page,
                        size
                )
        );
    }

    @Operation(
            summary = "Get enrollments by student"
    )
    @GetMapping("/student/{studentId}")
    public ResponseEntity<Page<EnrollmentResponse>>
    getEnrollmentsByStudent(

            @PathVariable Long studentId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return ResponseEntity.ok(
                enrollmentService.getEnrollmentsByStudent(
                        studentId,
                        page,
                        size
                )
        );
    }

    @Operation(
            summary = "Get enrollments by course"
    )
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Page<EnrollmentResponse>>
    getEnrollmentsByCourse(

            @PathVariable Long courseId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return ResponseEntity.ok(
                enrollmentService.getEnrollmentsByCourse(
                        courseId,
                        page,
                        size
                )
        );
    }

    @Operation(
            summary = "Update enrollment status"
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<EnrollmentResponse>
    updateEnrollmentStatus(

            @PathVariable Long id,

            @RequestParam EnrollmentStatus status) {

        return ResponseEntity.ok(
                enrollmentService.updateEnrollmentStatus(
                        id,
                        status
                )
        );
    }

    @Operation(
            summary = "Delete enrollment"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(
            @PathVariable Long id) {

        enrollmentService.deleteEnrollment(id);

        return ResponseEntity.noContent().build();
    }
}