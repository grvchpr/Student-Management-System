package com.kodewala.sms.service;

import com.kodewala.sms.dto.CourseRequest;
import com.kodewala.sms.dto.CourseResponse;
import org.springframework.data.domain.Page;

public interface CourseService {

    CourseResponse createCourse(CourseRequest request);

    CourseResponse getCourseById(Long id);

    Page<CourseResponse> getAllCourses(
            int page,
            int size,
            String sortBy,
            String direction
    );

    Page<CourseResponse> searchByName(
            String name,
            int page,
            int size
    );

    CourseResponse updateCourse(
            Long id,
            CourseRequest request
    );

    void deleteCourse(Long id);
}