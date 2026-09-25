package com.kodewala.sms.dto;

public record DashboardStatsResponse(
        long totalStudents,
        long totalCourses,
        long totalEnrollments,
        long pendingRegistrations
) {
}