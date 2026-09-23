package com.kodewala.sms.service;

import org.springframework.data.domain.Page;

import com.kodewala.sms.dto.EnrollmentRequest;
import com.kodewala.sms.dto.EnrollmentResponse;
import com.kodewala.sms.entity.EnrollmentStatus;

public interface EnrollmentService {

    EnrollmentResponse enrollStudent(
            EnrollmentRequest request
    );

    EnrollmentResponse getEnrollmentById(Long id);

    Page<EnrollmentResponse> getAllEnrollments(
            int page,
            int size
    );

    Page<EnrollmentResponse> getEnrollmentsByStudent(
            Long studentId,
            int page,
            int size
    );

    Page<EnrollmentResponse> getEnrollmentsByCourse(
            Long courseId,
            int page,
            int size
    );

    EnrollmentResponse updateEnrollmentStatus(
            Long id,
            EnrollmentStatus status
    );

    void deleteEnrollment(Long id);
}