package com.kodewala.sms.service;

import com.kodewala.sms.dto.CourseRequest;
import com.kodewala.sms.dto.CourseResponse;
import com.kodewala.sms.entity.Course;
import com.kodewala.sms.exception.CourseNotFoundException;
import com.kodewala.sms.exception.DuplicateCourseCodeException;
import com.kodewala.sms.repository.CourseRepository;
import com.kodewala.sms.service.impl.CourseServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course course;
    private CourseRequest courseRequest;

    @BeforeEach
    void setUp() {

        course = Course.builder()
                .id(1L)
                .courseName("Java Full Stack")
                .courseCode("JAVA001")
                .duration("6 Months")
                .fees(new BigDecimal("75000.00"))
                .description("Java, Spring Boot and Microservices")
                .build();

        courseRequest = CourseRequest.builder()
                .courseName("Java Full Stack")
                .courseCode("JAVA001")
                .duration("6 Months")
                .fees(new BigDecimal("75000.00"))
                .description("Java, Spring Boot and Microservices")
                .build();
    }

    // ---------------------------------------------------------
    // CREATE COURSE
    // ---------------------------------------------------------

    @Test
    void createCourse_shouldCreateCourseSuccessfully() {

        when(courseRepository.existsByCourseCode("JAVA001"))
                .thenReturn(false);

        when(courseRepository.save(any(Course.class)))
                .thenReturn(course);

        CourseResponse response =
                courseService.createCourse(courseRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Java Full Stack", response.getCourseName());
        assertEquals("JAVA001", response.getCourseCode());
        assertEquals("6 Months", response.getDuration());
        assertEquals(
                new BigDecimal("75000.00"),
                response.getFees()
        );

        verify(courseRepository)
                .existsByCourseCode("JAVA001");

        verify(courseRepository)
                .save(any(Course.class));
    }

    // ---------------------------------------------------------
    // CREATE COURSE - DUPLICATE COURSE CODE
    // ---------------------------------------------------------

    @Test
    void createCourse_shouldThrowExceptionWhenCourseCodeExists() {

        when(courseRepository.existsByCourseCode("JAVA001"))
                .thenReturn(true);

        assertThrows(
                DuplicateCourseCodeException.class,
                () -> courseService.createCourse(courseRequest)
        );

        verify(courseRepository, never())
                .save(any(Course.class));
    }

    // ---------------------------------------------------------
    // GET COURSE
    // ---------------------------------------------------------

    @Test
    void getCourseById_shouldReturnCourse() {

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        CourseResponse response =
                courseService.getCourseById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Java Full Stack", response.getCourseName());
        assertEquals("JAVA001", response.getCourseCode());

        verify(courseRepository)
                .findById(1L);
    }

    // ---------------------------------------------------------
    // GET COURSE - NOT FOUND
    // ---------------------------------------------------------

    @Test
    void getCourseById_shouldThrowExceptionWhenCourseNotFound() {

        when(courseRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CourseNotFoundException.class,
                () -> courseService.getCourseById(999L)
        );

        verify(courseRepository)
                .findById(999L);
    }

    // ---------------------------------------------------------
    // UPDATE COURSE
    // ---------------------------------------------------------

    @Test
    void updateCourse_shouldUpdateCourseSuccessfully() {

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        when(courseRepository.existsByCourseCodeAndIdNot(
                "JAVA001",
                1L
        )).thenReturn(false);

        when(courseRepository.save(any(Course.class)))
                .thenReturn(course);

        CourseResponse response =
                courseService.updateCourse(
                        1L,
                        courseRequest
                );

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Java Full Stack", response.getCourseName());
        assertEquals("JAVA001", response.getCourseCode());

        verify(courseRepository)
                .findById(1L);

        verify(courseRepository)
                .existsByCourseCodeAndIdNot(
                        "JAVA001",
                        1L
                );

        verify(courseRepository)
                .save(any(Course.class));
    }

    // ---------------------------------------------------------
    // UPDATE COURSE - DUPLICATE COURSE CODE
    // ---------------------------------------------------------

    @Test
    void updateCourse_shouldThrowExceptionWhenCourseCodeExists() {

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        when(courseRepository.existsByCourseCodeAndIdNot(
                "JAVA001",
                1L
        )).thenReturn(true);

        assertThrows(
                DuplicateCourseCodeException.class,
                () -> courseService.updateCourse(
                        1L,
                        courseRequest
                )
        );

        verify(courseRepository, never())
                .save(any(Course.class));
    }

    // ---------------------------------------------------------
    // DELETE COURSE
    // ---------------------------------------------------------

    @Test
    void deleteCourse_shouldDeleteCourseSuccessfully() {

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        courseService.deleteCourse(1L);

        verify(courseRepository)
                .findById(1L);

        verify(courseRepository)
                .delete(course);
    }

    // ---------------------------------------------------------
    // DELETE COURSE - NOT FOUND
    // ---------------------------------------------------------

    @Test
    void deleteCourse_shouldThrowExceptionWhenCourseNotFound() {

        when(courseRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CourseNotFoundException.class,
                () -> courseService.deleteCourse(999L)
        );

        verify(courseRepository)
                .findById(999L);

        verify(courseRepository, never())
                .delete(any(Course.class));
    }
}