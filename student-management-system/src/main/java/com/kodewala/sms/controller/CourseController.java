package com.kodewala.sms.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kodewala.sms.dto.CourseRequest;
import com.kodewala.sms.dto.CourseResponse;
import com.kodewala.sms.dto.CourseResponsePage;
import com.kodewala.sms.service.CourseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/courses")
@Tag(
    name = "Course Management",
    description = "APIs for creating, retrieving, updating and deleting courses"
)
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(
    	    summary = "Create a new course",
    	    description = "Creates a new course after validating the request and checking course code uniqueness"
    	)
    @ApiResponse(
    	    responseCode = "201",
    	    description = "Course created successfully",
    	    content = @Content(
    	        mediaType = "application/json",
    	        schema = @Schema(implementation = CourseResponse.class)
    	    )
    	)
    	@PostMapping
    	@PreAuthorize("hasRole('ADMIN')")
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
    	    description = "Retrieves a course using the course's unique ID"
    	)
    @ApiResponse(
    	    responseCode = "200",
    	    description = "Course found successfully",
    	    content = @Content(
    	        mediaType = "application/json",
    	        schema = @Schema(implementation = CourseResponse.class)
    	    )
    	)
    	@GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CourseResponse> getCourseById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                courseService.getCourseById(id)
        );
    }

    @Operation(
    	    summary = "Get all courses",
    	    description = "Retrieves a paginated and sorted list of courses"
    	)
    @ApiResponse(
    	    responseCode = "200",
    	    description = "Courses retrieved successfully",
    	    content = @Content(
    	        mediaType = "application/json",
    	        schema = @Schema(
    	            implementation = CourseResponsePage.class
    	        )
    	    )
    	)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
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
    	    description = "Retrieves courses based on the supplied search criteria"
    	)
    @ApiResponse(
    	    responseCode = "200",
    	    description = "Courses retrieved successfully",
    	    content = @Content(
    	        mediaType = "application/json",
    	        schema = @Schema(
    	            implementation = CourseResponsePage.class
    	        )
    	    )
    	)
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
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
    	    description = "Updates an existing course using the course's unique ID"
    	)
    @ApiResponse(
    	    responseCode = "200",
    	    description = "Course updated successfully",
    	    content = @Content(
    	        mediaType = "application/json",
    	        schema = @Schema(implementation = CourseResponse.class)
    	    )
    	)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
    	    description = "Deletes a course using the course's unique ID"
    	)
    	@ApiResponses({
    	    @ApiResponse(responseCode = "204", description = "Course deleted successfully"),
    	    @ApiResponse(responseCode = "404", description = "Course not found"),
    	    @ApiResponse(
    	        responseCode = "409",
    	        description = "Course cannot be deleted because enrollments exist"
    	    )
    	})
    	@DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id) {

        courseService.deleteCourse(id);

        return ResponseEntity.noContent().build();
    }
}