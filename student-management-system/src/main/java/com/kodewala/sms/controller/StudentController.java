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

import com.kodewala.sms.dto.StudentRequest;
import com.kodewala.sms.dto.StudentResponse;
import com.kodewala.sms.dto.StudentResponsePage;
import com.kodewala.sms.service.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/students")
@Tag(
	    name = "Student Management",
	    description = "APIs for creating, retrieving, updating and deleting students"
	)
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
    
    

    @Operation(
    	    summary = "Create a new student",
    	    description = "Creates a new student after validating the request and checking email uniqueness"
    	)
    @ApiResponse(
    	    responseCode = "201",
    	    description = "Student created successfully",
    	    content = @Content(
    	        mediaType = "application/json",
    	        schema = @Schema(implementation = StudentResponse.class)
    	    )
    	)
    	@PostMapping
    	@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentResponse> createStudent(
            @Valid @RequestBody StudentRequest request) {

        StudentResponse response =
                studentService.createStudent(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }
    
    @Operation(
    	    summary = "Search students by first name",
    	    description = "Retrieves students whose first name matches the supplied search criteria"
    	)
    @ApiResponse(
    	    responseCode = "200",
    	    description = "Students retrieved successfully",
    	    content = @Content(
    	        mediaType = "application/json",
    	        schema = @Schema(
    	            implementation = StudentResponsePage.class
    	        )
    	    )
    	)
    	@GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<StudentResponse>> searchByName(

            @RequestParam String name,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return ResponseEntity.ok(
                studentService.searchByName(
                        name,
                        page,
                        size
                )
        );
    }

    @Operation(
    	    summary = "Get student by ID",
    	    description = "Retrieves a student using the student's unique ID"
    	)
    @ApiResponse(
    	    responseCode = "200",
    	    description = "Student found successfully",
    	    content = @Content(
    	        mediaType = "application/json",
    	        schema = @Schema(implementation = StudentResponse.class)
    	    )
    	)
    	@GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<StudentResponse> getStudentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                studentService.getStudentById(id)
        );
    }

    @Operation(
    	    summary = "Get all students",
    	    description = "Retrieves a paginated and sorted list of students"
    	)
    @ApiResponse(
    	    responseCode = "200",
    	    description = "Students retrieved successfully",
    	    content = @Content(
    	        mediaType = "application/json",
    	        schema = @Schema(
    	            implementation = StudentResponsePage.class
    	        )
    	    )
    	)
    	@GetMapping
    	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<StudentResponse>> getAllStudents(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "id")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction) {

        return ResponseEntity.ok(
                studentService.getAllStudents(
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }

    @Operation(
    	    summary = "Update student",
    	    description = "Updates an existing student's information using the student's unique ID"
    	)
    @ApiResponse(
    	    responseCode = "200",
    	    description = "Student updated successfully",
    	    content = @Content(
    	        mediaType = "application/json",
    	        schema = @Schema(implementation = StudentResponse.class)
    	    )
    	)
    	@PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequest request) {

        return ResponseEntity.ok(
                studentService.updateStudent(id, request)
        );
    }

    @Operation(
    	    summary = "Delete student",
    	    description = "Deletes a student using the student's unique ID"
    	)
    @ApiResponse(
    	    responseCode = "204",
    	    description = "Student deleted successfully"
    	)
    	@DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable Long id) {

        studentService.deleteStudent(id);

        return ResponseEntity.noContent().build();
    }
}