package com.kodewala.sms.service;

import com.kodewala.sms.dto.EnrollmentRequest;
import com.kodewala.sms.dto.EnrollmentResponse;
import com.kodewala.sms.entity.Course;
import com.kodewala.sms.entity.Enrollment;
import com.kodewala.sms.entity.EnrollmentStatus;
import com.kodewala.sms.entity.Student;
import com.kodewala.sms.exception.CourseNotFoundException;
import com.kodewala.sms.exception.DuplicateEnrollmentException;
import com.kodewala.sms.exception.EnrollmentNotFoundException;
import com.kodewala.sms.exception.StudentNotFoundException;
import com.kodewala.sms.repository.CourseRepository;
import com.kodewala.sms.repository.EnrollmentRepository;
import com.kodewala.sms.repository.StudentRepository;
import com.kodewala.sms.service.impl.EnrollmentServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private Student student;
    private Course course;
    private Enrollment enrollment;
    private EnrollmentRequest enrollmentRequest;

    @BeforeEach
    void setUp() {

        student = Student.builder()
                .id(1L)
                .firstName("Gourav")
                .lastName("Chopra")
                .email("gourav@gmail.com")
                .build();

        course = Course.builder()
                .id(1L)
                .courseName("Java Full Stack")
                .courseCode("JAVA001")
                .duration("6 Months")
                .build();

        enrollment = Enrollment.builder()
                .id(1L)
                .student(student)
                .course(course)
                .enrollmentDate(LocalDate.of(2026, 9, 23))
                .status(EnrollmentStatus.ACTIVE)
                .build();

        enrollmentRequest = EnrollmentRequest.builder()
                .studentId(1L)
                .courseId(1L)
                .enrollmentDate(LocalDate.of(2026, 9, 23))
                .status(EnrollmentStatus.ACTIVE)
                .build();
    }

    // =========================================================
    // CREATE ENROLLMENT
    // =========================================================

    @Test
    void enrollStudent_shouldCreateEnrollmentSuccessfully() {

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        when(enrollmentRepository.existsByStudentIdAndCourseId(
                1L,
                1L
        )).thenReturn(false);

        when(enrollmentRepository.save(any(Enrollment.class)))
                .thenReturn(enrollment);

        EnrollmentResponse response =
                enrollmentService.enrollStudent(enrollmentRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getStudentId());
        assertEquals(1L, response.getCourseId());
        assertEquals(
                EnrollmentStatus.ACTIVE,
                response.getStatus()
        );

        verify(studentRepository)
                .findById(1L);

        verify(courseRepository)
                .findById(1L);

        verify(enrollmentRepository)
                .existsByStudentIdAndCourseId(1L, 1L);

        verify(enrollmentRepository)
                .save(any(Enrollment.class));
    }

    // =========================================================
    // STUDENT NOT FOUND
    // =========================================================

    @Test
    void enrollStudent_shouldThrowExceptionWhenStudentNotFound() {

        when(studentRepository.findById(999L))
                .thenReturn(Optional.empty());

        EnrollmentRequest request =
                EnrollmentRequest.builder()
                        .studentId(999L)
                        .courseId(1L)
                        .build();

        assertThrows(
                StudentNotFoundException.class,
                () -> enrollmentService.enrollStudent(request)
        );

        verify(studentRepository)
                .findById(999L);

        verify(courseRepository, never())
                .findById(anyLong());

        verify(enrollmentRepository, never())
                .save(any(Enrollment.class));
    }

    // =========================================================
    // COURSE NOT FOUND
    // =========================================================

    @Test
    void enrollStudent_shouldThrowExceptionWhenCourseNotFound() {

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        when(courseRepository.findById(999L))
                .thenReturn(Optional.empty());

        EnrollmentRequest request =
                EnrollmentRequest.builder()
                        .studentId(1L)
                        .courseId(999L)
                        .build();

        assertThrows(
                CourseNotFoundException.class,
                () -> enrollmentService.enrollStudent(request)
        );

        verify(studentRepository)
                .findById(1L);

        verify(courseRepository)
                .findById(999L);

        verify(enrollmentRepository, never())
                .save(any(Enrollment.class));
    }

    // =========================================================
    // DUPLICATE ENROLLMENT
    // =========================================================

    @Test
    void enrollStudent_shouldThrowExceptionWhenAlreadyEnrolled() {

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        when(enrollmentRepository.existsByStudentIdAndCourseId(
                1L,
                1L
        )).thenReturn(true);

        assertThrows(
                DuplicateEnrollmentException.class,
                () -> enrollmentService.enrollStudent(enrollmentRequest)
        );

        verify(enrollmentRepository)
                .existsByStudentIdAndCourseId(1L, 1L);

        verify(enrollmentRepository, never())
                .save(any(Enrollment.class));
    }

    // =========================================================
    // GET ENROLLMENT
    // =========================================================

    @Test
    void getEnrollmentById_shouldReturnEnrollment() {

        when(enrollmentRepository.findById(1L))
                .thenReturn(Optional.of(enrollment));

        EnrollmentResponse response =
                enrollmentService.getEnrollmentById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getStudentId());
        assertEquals(1L, response.getCourseId());
        assertEquals(
                EnrollmentStatus.ACTIVE,
                response.getStatus()
        );

        verify(enrollmentRepository)
                .findById(1L);
    }

    // =========================================================
    // GET ENROLLMENT - NOT FOUND
    // =========================================================

    @Test
    void getEnrollmentById_shouldThrowExceptionWhenNotFound() {

        when(enrollmentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                EnrollmentNotFoundException.class,
                () -> enrollmentService.getEnrollmentById(999L)
        );

        verify(enrollmentRepository)
                .findById(999L);
    }

    // =========================================================
    // UPDATE STATUS
    // =========================================================

    @Test
    void updateEnrollmentStatus_shouldUpdateStatusSuccessfully() {

        when(enrollmentRepository.findById(1L))
                .thenReturn(Optional.of(enrollment));

        when(enrollmentRepository.save(any(Enrollment.class)))
                .thenReturn(enrollment);

        EnrollmentResponse response =
                enrollmentService.updateEnrollmentStatus(
                        1L,
                        EnrollmentStatus.COMPLETED
                );

        assertNotNull(response);

        verify(enrollmentRepository)
                .findById(1L);

        verify(enrollmentRepository)
                .save(any(Enrollment.class));

        assertEquals(
                EnrollmentStatus.COMPLETED,
                enrollment.getStatus()
        );
    }

    // =========================================================
    // DELETE ENROLLMENT
    // =========================================================

    @Test
    void deleteEnrollment_shouldDeleteSuccessfully() {

        when(enrollmentRepository.findById(1L))
                .thenReturn(Optional.of(enrollment));

        enrollmentService.deleteEnrollment(1L);

        verify(enrollmentRepository)
                .findById(1L);

        verify(enrollmentRepository)
                .delete(enrollment);
    }
}