package com.kodewala.sms.service.impl;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kodewala.sms.dto.CourseRequest;
import com.kodewala.sms.dto.CourseResponse;
import com.kodewala.sms.entity.Course;
import com.kodewala.sms.exception.CourseNotFoundException;
import com.kodewala.sms.exception.DuplicateCourseCodeException;
import com.kodewala.sms.repository.CourseRepository;
import com.kodewala.sms.service.CourseService;
import com.kodewala.sms.util.PageRequestUtil;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public CourseResponse createCourse(CourseRequest request) {

        if (courseRepository.existsByCourseCode(
                request.getCourseCode())) {

            throw new DuplicateCourseCodeException(
                    "Course already exists with code: "
                            + request.getCourseCode()
            );
        }

        Course course = new Course();

        course.setCourseName(request.getCourseName());
        course.setCourseCode(request.getCourseCode());
        course.setDuration(request.getDuration());
        course.setFees(request.getFees());
        course.setDescription(request.getDescription());

        Course savedCourse = courseRepository.save(course);

        return mapToResponse(savedCourse);
    }

    @Override
    public CourseResponse getCourseById(Long id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new CourseNotFoundException(
                                "Course not found with id: " + id
                        ));

        return mapToResponse(course);
    }

    @Override
    public Page<CourseResponse> getAllCourses(
            int page,
            int size,
            String sortBy,
            String direction) {

        Set<String> allowedSortFields = Set.of(
                "id",
                "courseName",
                "courseCode",
                "duration",
                "fees",
                "createdAt"
        );

        Pageable pageable = PageRequestUtil.create(
                page,
                size,
                sortBy,
                direction,
                allowedSortFields
        );

        return courseRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<CourseResponse> searchByName(
            String name,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("courseName").ascending()
        );

        return courseRepository
                .findByCourseNameContainingIgnoreCase(
                        name,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    public CourseResponse updateCourse(
            Long id,
            CourseRequest request) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new CourseNotFoundException(
                                "Course not found with id: " + id
                        ));

        if (courseRepository.existsByCourseCodeAndIdNot(
                request.getCourseCode(),
                id)) {

            throw new DuplicateCourseCodeException(
                    "Another course already exists with code: "
                            + request.getCourseCode()
            );
        }

        course.setCourseName(request.getCourseName());
        course.setCourseCode(request.getCourseCode());
        course.setDuration(request.getDuration());
        course.setFees(request.getFees());
        course.setDescription(request.getDescription());

        Course updatedCourse =
                courseRepository.save(course);

        return mapToResponse(updatedCourse);
    }

    @Override
    public void deleteCourse(Long id) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() ->
                        new CourseNotFoundException(
                                "Course not found with id: " + id
                        ));

        courseRepository.delete(course);
    }

    private CourseResponse mapToResponse(Course course) {

        return CourseResponse.builder()
                .id(course.getId())
                .courseName(course.getCourseName())
                .courseCode(course.getCourseCode())
                .duration(course.getDuration())
                .fees(course.getFees())
                .description(course.getDescription())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}