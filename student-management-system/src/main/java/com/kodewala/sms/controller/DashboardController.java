package com.kodewala.sms.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kodewala.sms.dto.DashboardStatsResponse;
import com.kodewala.sms.repository.CourseRepository;
import com.kodewala.sms.repository.EnrollmentRepository;
import com.kodewala.sms.repository.StudentRepository;
import com.kodewala.sms.repository.UserRepository;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    public DashboardController(
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository) {

        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public DashboardStatsResponse getStats() {

        long totalStudents = studentRepository.count();
        long totalCourses = courseRepository.count();
        long totalEnrollments = enrollmentRepository.count();
        long pendingRegistrations =
                userRepository.countByStatus("PENDING");

        return new DashboardStatsResponse(
                totalStudents,
                totalCourses,
                totalEnrollments,
                pendingRegistrations
        );
    }
}