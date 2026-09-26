package com.kodewala.sms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kodewala.sms.dto.StudentRequest;
import com.kodewala.sms.dto.StudentResponse;
import com.kodewala.sms.entity.Student;
import com.kodewala.sms.exception.DuplicateEmailException;
import com.kodewala.sms.exception.ResourceConflictException;
import com.kodewala.sms.exception.StudentNotFoundException;
import com.kodewala.sms.repository.EnrollmentRepository;
import com.kodewala.sms.repository.StudentRepository;
import com.kodewala.sms.service.impl.StudentServiceImpl;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student student;
    private StudentRequest studentRequest;
    
    @Mock
    private EnrollmentRepository enrollmentRepository;

    @BeforeEach
    void setUp() {

        student = Student.builder()
                .id(1L)
                .firstName("Gourav")
                .lastName("Chopra")
                .email("gourav@gmail.com")
                .phone("9876543210")
                .dateOfBirth(LocalDate.of(1996, 12, 27))
                .address("Bangalore")
                .build();

        studentRequest = StudentRequest.builder()
                .firstName("Gourav")
                .lastName("Chopra")
                .email("gourav@gmail.com")
                .phone("9876543210")
                .dateOfBirth(LocalDate.of(1996, 12, 27))
                .address("Bangalore")
                .build();
    }

    // ---------------------------------------------------------
    // CREATE STUDENT
    // ---------------------------------------------------------

    @Test
    void createStudent_shouldCreateStudentSuccessfully() {

        when(studentRepository.existsByEmail("gourav@gmail.com"))
                .thenReturn(false);

        when(studentRepository.save(any(Student.class)))
                .thenReturn(student);

        StudentResponse response =
                studentService.createStudent(studentRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Gourav", response.getFirstName());
        assertEquals("Chopra", response.getLastName());
        assertEquals("gourav@gmail.com", response.getEmail());

        verify(studentRepository)
                .existsByEmail("gourav@gmail.com");

        verify(studentRepository)
                .save(any(Student.class));
    }

    // ---------------------------------------------------------
    // CREATE STUDENT - DUPLICATE EMAIL
    // ---------------------------------------------------------

    @Test
    void createStudent_shouldThrowExceptionWhenEmailAlreadyExists() {

        when(studentRepository.existsByEmail("gourav@gmail.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> studentService.createStudent(studentRequest)
        );

        verify(studentRepository, never())
                .save(any(Student.class));
    }

    // ---------------------------------------------------------
    // GET STUDENT
    // ---------------------------------------------------------

    @Test
    void getStudentById_shouldReturnStudent() {

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        StudentResponse response =
                studentService.getStudentById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Gourav", response.getFirstName());
        assertEquals("gourav@gmail.com", response.getEmail());

        verify(studentRepository)
                .findById(1L);
    }

    // ---------------------------------------------------------
    // GET STUDENT - NOT FOUND
    // ---------------------------------------------------------

    @Test
    void getStudentById_shouldThrowExceptionWhenStudentNotFound() {

        when(studentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                StudentNotFoundException.class,
                () -> studentService.getStudentById(999L)
        );

        verify(studentRepository)
                .findById(999L);
    }

    // ---------------------------------------------------------
    // UPDATE STUDENT
    // ---------------------------------------------------------

    @Test
    void updateStudent_shouldUpdateStudentSuccessfully() {

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        when(studentRepository.existsByEmailAndIdNot(
                "gourav@gmail.com",
                1L
        )).thenReturn(false);

        when(studentRepository.save(any(Student.class)))
                .thenReturn(student);

        StudentResponse response =
                studentService.updateStudent(
                        1L,
                        studentRequest
                );

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Gourav", response.getFirstName());
        assertEquals("gourav@gmail.com", response.getEmail());

        verify(studentRepository)
                .findById(1L);

        verify(studentRepository)
                .existsByEmailAndIdNot(
                        "gourav@gmail.com",
                        1L
                );

        verify(studentRepository)
                .save(any(Student.class));
    }

    // ---------------------------------------------------------
    // UPDATE STUDENT - DUPLICATE EMAIL
    // ---------------------------------------------------------

    @Test
    void updateStudent_shouldThrowExceptionWhenEmailAlreadyExists() {

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        when(studentRepository.existsByEmailAndIdNot(
                "gourav@gmail.com",
                1L
        )).thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> studentService.updateStudent(
                        1L,
                        studentRequest
                )
        );

        verify(studentRepository, never())
                .save(any(Student.class));
    }

    // ---------------------------------------------------------
    // DELETE STUDENT
    // ---------------------------------------------------------

    @Test
    void deleteStudent_shouldDeleteStudentSuccessfully() {

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        studentService.deleteStudent(1L);

        verify(studentRepository)
                .findById(1L);

        verify(studentRepository)
                .delete(student);
    }

    // ---------------------------------------------------------
    // DELETE STUDENT - NOT FOUND
    // ---------------------------------------------------------

    @Test
    void deleteStudent_shouldThrowExceptionWhenStudentNotFound() {

        when(studentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                StudentNotFoundException.class,
                () -> studentService.deleteStudent(999L)
        );

        verify(studentRepository)
                .findById(999L);

        verify(studentRepository, never())
                .delete(any(Student.class));
    }
    
    @Test
    void deleteStudent_shouldRejectWhenEnrollmentsExist() {

        Student student = new Student();
        student.setId(1L);

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        when(enrollmentRepository.existsByStudentId(1L))
                .thenReturn(true);

        ResourceConflictException exception =
                assertThrows(
                        ResourceConflictException.class,
                        () -> studentService.deleteStudent(1L)
                );

        assertEquals(
                "Student cannot be deleted because enrollments exist",
                exception.getMessage()
        );

        verify(studentRepository, never())
                .delete(any(Student.class));
    }
    
    @Test
    void deleteStudent_shouldDeleteWhenNoEnrollmentsExist() {

        Student student = new Student();
        student.setId(1L);

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        when(enrollmentRepository.existsByStudentId(1L))
                .thenReturn(false);

        studentService.deleteStudent(1L);

        verify(studentRepository)
                .delete(student);
    }
}