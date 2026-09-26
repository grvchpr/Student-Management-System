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
import com.kodewala.sms.exception.ResourceConflictException;
import com.kodewala.sms.repository.CourseRepository;
import com.kodewala.sms.repository.EnrollmentRepository;
import com.kodewala.sms.service.CourseService;
import com.kodewala.sms.util.PageRequestUtil;

@Service
public class CourseServiceImpl implements CourseService {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "id",
                    "courseName",
                    "courseCode",
                    "duration",
                    "fees",
                    "createdAt"
            );

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseServiceImpl(
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository) {

        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public CourseResponse createCourse(CourseRequest request) {

        String courseCode =
                normalizeCourseCode(request.getCourseCode());

        if (courseRepository.existsByCourseCode(courseCode)) {

            throw new DuplicateCourseCodeException(
                    "Course already exists with code: "
                            + courseCode
            );
        }

        Course course = new Course();

        course.setCourseName(request.getCourseName());
        course.setCourseCode(courseCode);
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

        Pageable pageable = PageRequestUtil.create(
                page,
                size,
                sortBy,
                direction,
                ALLOWED_SORT_FIELDS
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

        String courseCode =
                normalizeCourseCode(request.getCourseCode());

        if (courseRepository.existsByCourseCodeAndIdNot(
                courseCode,
                id)) {

            throw new DuplicateCourseCodeException(
                    "Another course already exists with code: "
                            + courseCode
            );
        }

        course.setCourseName(request.getCourseName());
        course.setCourseCode(courseCode);
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

        if (enrollmentRepository.existsByCourseId(id)) {
            throw new ResourceConflictException(
                    "Course cannot be deleted because enrollments exist"
            );
        }

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

    private String normalizeCourseCode(String courseCode) {

        return courseCode
                .trim()
                .toUpperCase();
    }
}