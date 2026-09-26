package com.kodewala.sms.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import jakarta.servlet.http.HttpServletRequest;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();

        request = mock(HttpServletRequest.class);

        when(request.getRequestURI())
                .thenReturn("/api/v1/test");
    }

    @Test
    void handleStudentNotFound_shouldReturn404() {

        StudentNotFoundException exception =
                new StudentNotFoundException(
                        "Student not found: 1"
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleStudentNotFound(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertErrorResponse(
                response,
                "Student not found: 1",
                "/api/v1/test"
        );
    }

    @Test
    void handleCourseNotFound_shouldReturn404() {

        CourseNotFoundException exception =
                new CourseNotFoundException(
                        "Course not found: 1"
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleCourseNotFound(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertErrorResponse(
                response,
                "Course not found: 1",
                "/api/v1/test"
        );
    }

    @Test
    void handleEnrollmentNotFound_shouldReturn404() {

        EnrollmentNotFoundException exception =
                new EnrollmentNotFoundException(
                        "Enrollment not found: 1"
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleEnrollmentNotFound(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertErrorResponse(
                response,
                "Enrollment not found: 1",
                "/api/v1/test"
        );
    }

    @Test
    void handleUserNotFound_shouldReturn404() {

        UserNotFoundException exception =
                new UserNotFoundException(
                        "User not found: 1"
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleUserNotFound(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertErrorResponse(
                response,
                "User not found: 1",
                "/api/v1/test"
        );
    }

    @Test
    void handleDuplicateEmail_shouldReturn409() {

        DuplicateEmailException exception =
                new DuplicateEmailException(
                        "Student email already exists"
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleDuplicateEmail(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        assertErrorResponse(
                response,
                "Student email already exists",
                "/api/v1/test"
        );
    }

    @Test
    void handleDuplicateCourseCode_shouldReturn409() {

        DuplicateCourseCodeException exception =
                new DuplicateCourseCodeException(
                        "Course code already exists"
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleDuplicateCourseCode(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        assertErrorResponse(
                response,
                "Course code already exists",
                "/api/v1/test"
        );
    }

    @Test
    void handleDuplicateEnrollment_shouldReturn409() {

        DuplicateEnrollmentException exception =
                new DuplicateEnrollmentException(
                        "Student is already enrolled in this course"
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleDuplicateEnrollment(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        assertErrorResponse(
                response,
                "Student is already enrolled in this course",
                "/api/v1/test"
        );
    }

    @Test
    void handleDuplicateUsername_shouldReturn409() {

        DuplicateUsernameException exception =
                new DuplicateUsernameException(
                        "Username already exists"
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleDuplicateUsername(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        assertErrorResponse(
                response,
                "Username already exists",
                "/api/v1/test"
        );
    }

    @Test
    void handleResourceConflict_shouldReturn409() {

        ResourceConflictException exception =
                new ResourceConflictException(
                        "Resource conflict"
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleResourceConflict(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        assertErrorResponse(
                response,
                "Resource conflict",
                "/api/v1/test"
        );
    }

    @Test
    void handleIllegalArgument_shouldReturn400() {

        IllegalArgumentException exception =
                new IllegalArgumentException(
                        "Invalid argument"
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleIllegalArgument(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertErrorResponse(
                response,
                "Invalid argument",
                "/api/v1/test"
        );
    }

    @Test
    void handleAccountPending_shouldReturn403() {

        AccountPendingException exception =
                new AccountPendingException(
                        "Your account is pending administrator approval."
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleAccountPending(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.FORBIDDEN,
                response.getStatusCode()
        );

        assertErrorResponse(
                response,
                "Your account is pending administrator approval.",
                "/api/v1/test"
        );
    }

    @Test
    void handleAccountRejected_shouldReturn403() {

        AccountRejectedException exception =
                new AccountRejectedException(
                        "Your account registration was rejected."
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleAccountRejected(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.FORBIDDEN,
                response.getStatusCode()
        );

        assertErrorResponse(
                response,
                "Your account registration was rejected.",
                "/api/v1/test"
        );
    }

    @Test
    void handleGeneralException_shouldReturn500() {

        Exception exception =
                new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response =
                handler.handleGeneralException(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode()
        );

        assertErrorResponse(
                response,
                "An unexpected error occurred",
                "/api/v1/test"
        );
    }

    private void assertErrorResponse(
            ResponseEntity<ErrorResponse> response,
            String expectedMessage,
            String expectedPath) {

        assertNotNull(response);
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();

        assertEquals(
                expectedMessage,
                body.getMessage()
        );

        assertEquals(
                expectedPath,
                body.getPath()
        );

        assertNotNull(body.getTimestamp());
    }
    
    @Test
    void handleAccessDenied_shouldReturn403() {

        AccessDeniedException exception =
                new AccessDeniedException("Access denied");

        HttpServletRequest request =
                mock(HttpServletRequest.class);

        when(request.getRequestURI())
                .thenReturn("/api/v1/dashboard/stats");

        ResponseEntity<ErrorResponse> response =
                handler.handleAccessDenied(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.FORBIDDEN,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                403,
                response.getBody().getStatus()
        );

        assertEquals(
                "Access denied",
                response.getBody().getMessage()
        );

        assertEquals(
                "/api/v1/dashboard/stats",
                response.getBody().getPath()
        );

        assertNotNull(
                response.getBody().getTimestamp()
        );
    }
}