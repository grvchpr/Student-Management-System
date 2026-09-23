package com.kodewala.sms.controller;

import com.kodewala.sms.dto.CourseRequest;
import com.kodewala.sms.dto.CourseResponse;
import com.kodewala.sms.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/courses")
@Tag(
        name = "Course Management",
        description = "APIs for managing courses"
)
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(
            summary = "Create a course",
            description = "Creates a new course"
    )
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CourseRequest request) {

        CourseResponse response =
                courseService.createCourse(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @Operation(
            summary = "Get course by ID",
            description = "Fetches a course using course ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                courseService.getCourseById(id)
        );
    }

    @Operation(
            summary = "Get all courses",
            description = "Fetches courses with pagination and sorting"
    )
    @GetMapping
    public ResponseEntity<Page<CourseResponse>> getAllCourses(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction) {

        return ResponseEntity.ok(
                courseService.getAllCourses(
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }

    @Operation(
            summary = "Search courses",
            description = "Search courses by course name"
    )
    @GetMapping("/search")
    public ResponseEntity<Page<CourseResponse>> searchCourses(

            @RequestParam String name,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return ResponseEntity.ok(
                courseService.searchByName(
                        name,
                        page,
                        size
                )
        );
    }

    @Operation(
            summary = "Update course",
            description = "Updates an existing course"
    )
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request) {

        return ResponseEntity.ok(
                courseService.updateCourse(
                        id,
                        request
                )
        );
    }

    @Operation(
            summary = "Delete course",
            description = "Deletes a course"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id) {

        courseService.deleteCourse(id);

        return ResponseEntity.noContent().build();
    }
}