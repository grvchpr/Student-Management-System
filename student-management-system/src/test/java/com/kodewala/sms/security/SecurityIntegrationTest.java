package com.kodewala.sms.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kodewala.sms.dto.CourseRequest;
import com.kodewala.sms.dto.CourseResponse;
import com.kodewala.sms.dto.EnrollmentRequest;
import com.kodewala.sms.dto.EnrollmentResponse;
import com.kodewala.sms.dto.StudentRequest;
import com.kodewala.sms.dto.StudentResponse;
import com.kodewala.sms.dto.UserResponse;
import com.kodewala.sms.entity.EnrollmentStatus;
import com.kodewala.sms.repository.CourseRepository;
import com.kodewala.sms.repository.EnrollmentRepository;
import com.kodewala.sms.repository.StudentRepository;
import com.kodewala.sms.repository.UserRepository;
import com.kodewala.sms.service.CourseService;
import com.kodewala.sms.service.EnrollmentService;
import com.kodewala.sms.service.StudentService;
import com.kodewala.sms.service.UserRegistrationService;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private CourseService courseService;

    @MockitoBean
    private EnrollmentService enrollmentService;

    @MockitoBean
    private UserRegistrationService userRegistrationService;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoBean
    private CourseRepository courseRepository;

    @MockitoBean
    private EnrollmentRepository enrollmentRepository;

    @MockitoBean
    private UserRepository userRepository;

    private ObjectMapper objectMapper;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {

        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
                );

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("testuser")
                        .password("password")
                        .roles("USER")
                        .build();

        UserDetails adminDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("testadmin")
                        .password("password")
                        .roles("ADMIN")
                        .build();

        when(customUserDetailsService
                .loadUserByUsername("testuser"))
                .thenReturn(userDetails);

        when(customUserDetailsService
                .loadUserByUsername("testadmin"))
                .thenReturn(adminDetails);

        userToken =
                jwtService.generateToken(userDetails);

        adminToken =
                jwtService.generateToken(adminDetails);
    }

    // =========================================================
    // AUTHENTICATION
    // =========================================================

    @Test
    void requestWithoutJwt_shouldReturn401() throws Exception {

        mockMvc.perform(
                get("/api/v1/students")
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void requestWithInvalidJwt_shouldReturn401() throws Exception {

        mockMvc.perform(
                get("/api/v1/students")
                        .header(
                                "Authorization",
                                "Bearer invalid-token"
                        )
        )
        .andExpect(status().isUnauthorized());
    }

    // =========================================================
    // USER READ ACCESS
    // =========================================================

    @Test
    void userShouldBeAllowedToReadStudents() throws Exception {

        when(studentService.getAllStudents(
                eq(0),
                eq(10),
                eq("id"),
                eq("asc")
        )).thenReturn(Page.empty());

        mockMvc.perform(
                get("/api/v1/students")
                        .header(
                                "Authorization",
                                "Bearer " + userToken
                        )
        )
        .andExpect(status().isOk());
    }

    @Test
    void userShouldBeAllowedToReadCourses() throws Exception {

        when(courseService.getAllCourses(
                eq(0),
                eq(10),
                eq("id"),
                eq("asc")
        )).thenReturn(Page.empty());

        mockMvc.perform(
                get("/api/v1/courses")
                        .header(
                                "Authorization",
                                "Bearer " + userToken
                        )
        )
        .andExpect(status().isOk());
    }

    @Test
    void userShouldBeAllowedToReadEnrollments() throws Exception {

        when(enrollmentService.getAllEnrollments(
                eq(0),
                eq(10)
        )).thenReturn(Page.empty());

        mockMvc.perform(
                get("/api/v1/enrollments")
                        .header(
                                "Authorization",
                                "Bearer " + userToken
                        )
        )
        .andExpect(status().isOk());
    }

    // =========================================================
    // USER WRITE RESTRICTIONS
    // =========================================================

    @Test
    void userShouldNotBeAllowedToCreateStudent() throws Exception {

        StudentRequest request =
                StudentRequest.builder()
                        .firstName("Gourav")
                        .lastName("Chopra")
                        .email("gourav@test.com")
                        .phone("9876543210")
                        .dateOfBirth(
                                LocalDate.of(1996, 12, 27)
                        )
                        .address("Bangalore")
                        .build();

        mockMvc.perform(
                post("/api/v1/students")
                        .header(
                                "Authorization",
                                "Bearer " + userToken
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        request
                                )
                        )
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void userShouldNotBeAllowedToCreateCourse() throws Exception {

        CourseRequest request =
                CourseRequest.builder()
                        .courseName("Java Full Stack")
                        .courseCode("JAVA101")
                        .duration("6 Months")
                        .fees(new BigDecimal("75000.00"))
                        .description(
                                "Java, Spring Boot and Microservices"
                        )
                        .build();

        mockMvc.perform(
                post("/api/v1/courses")
                        .header(
                                "Authorization",
                                "Bearer " + userToken
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        request
                                )
                        )
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void userShouldNotBeAllowedToCreateEnrollment()
            throws Exception {

        EnrollmentRequest request =
                EnrollmentRequest.builder()
                        .studentId(1L)
                        .courseId(1L)
                        .enrollmentDate(
                                LocalDate.now()
                        )
                        .build();

        mockMvc.perform(
                post("/api/v1/enrollments")
                        .header(
                                "Authorization",
                                "Bearer " + userToken
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        request
                                )
                        )
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void userShouldNotBeAllowedToDeleteStudent()
            throws Exception {

        mockMvc.perform(
                delete("/api/v1/students/1")
                        .header(
                                "Authorization",
                                "Bearer " + userToken
                        )
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void userShouldNotBeAllowedToUpdateEnrollmentStatus()
            throws Exception {

        mockMvc.perform(
                put("/api/v1/enrollments/1/status")
                        .header(
                                "Authorization",
                                "Bearer " + userToken
                        )
                        .param(
                                "status",
                                EnrollmentStatus.ACTIVE.name()
                        )
        )
        .andExpect(status().isForbidden());
    }

    // =========================================================
    // USER ADMIN AREA RESTRICTIONS
    // =========================================================

    @Test
    void userShouldNotBeAllowedToAccessDashboard()
            throws Exception {

        mockMvc.perform(
                get("/api/v1/dashboard/stats")
                        .header(
                                "Authorization",
                                "Bearer " + userToken
                        )
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void userShouldNotBeAllowedToAccessPendingUsers()
            throws Exception {

        mockMvc.perform(
                get("/api/v1/admin/users/pending")
                        .header(
                                "Authorization",
                                "Bearer " + userToken
                        )
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void userShouldNotBeAllowedToApproveUser()
            throws Exception {

        mockMvc.perform(
                put("/api/v1/admin/users/1/approve")
                        .header(
                                "Authorization",
                                "Bearer " + userToken
                        )
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void userShouldNotBeAllowedToRejectUser()
            throws Exception {

        mockMvc.perform(
                put("/api/v1/admin/users/1/reject")
                        .header(
                                "Authorization",
                                "Bearer " + userToken
                        )
        )
        .andExpect(status().isForbidden());
    }

    // =========================================================
    // ADMIN ACCESS
    // =========================================================

    @Test
    void adminShouldBeAllowedToReadStudents() throws Exception {

        when(studentService.getAllStudents(
                eq(0),
                eq(10),
                eq("id"),
                eq("asc")
        )).thenReturn(Page.empty());

        mockMvc.perform(
                get("/api/v1/students")
                        .header(
                                "Authorization",
                                "Bearer " + adminToken
                        )
        )
        .andExpect(status().isOk());
    }

    @Test
    void adminShouldBeAllowedToCreateStudent()
            throws Exception {

        StudentRequest request =
                StudentRequest.builder()
                        .firstName("Gourav")
                        .lastName("Chopra")
                        .email("admin@test.com")
                        .phone("9876543210")
                        .dateOfBirth(
                                LocalDate.of(1996, 12, 27)
                        )
                        .address("Bangalore")
                        .build();

        StudentResponse response =
                StudentResponse.builder()
                        .id(1L)
                        .firstName("Gourav")
                        .lastName("Chopra")
                        .email("admin@test.com")
                        .build();

        when(studentService.createStudent(
                any(StudentRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                post("/api/v1/students")
                        .header(
                                "Authorization",
                                "Bearer " + adminToken
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        request
                                )
                        )
        )
        .andExpect(status().isCreated());
    }

    @Test
    void adminShouldBeAllowedToCreateCourse()
            throws Exception {

        CourseRequest request =
                CourseRequest.builder()
                        .courseName("Java Full Stack")
                        .courseCode("JAVA101")
                        .duration("6 Months")
                        .fees(new BigDecimal("75000.00"))
                        .description(
                                "Java, Spring Boot and Microservices"
                        )
                        .build();

        CourseResponse response =
                CourseResponse.builder()
                        .id(1L)
                        .courseName("Java Full Stack")
                        .courseCode("JAVA101")
                        .duration("6 Months")
                        .fees(new BigDecimal("75000.00"))
                        .description(
                                "Java, Spring Boot and Microservices"
                        )
                        .build();

        when(courseService.createCourse(
                any(CourseRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                post("/api/v1/courses")
                        .header(
                                "Authorization",
                                "Bearer " + adminToken
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        request
                                )
                        )
        )
        .andExpect(status().isCreated());
    }

    @Test
    void adminShouldBeAllowedToCreateEnrollment()
            throws Exception {

        EnrollmentRequest request =
                EnrollmentRequest.builder()
                        .studentId(1L)
                        .courseId(1L)
                        .enrollmentDate(
                                LocalDate.now()
                        )
                        .build();

        EnrollmentResponse response =
                EnrollmentResponse.builder()
                        .id(1L)
                        .studentId(1L)
                        .courseId(1L)
                        .enrollmentDate(
                                LocalDate.now()
                        )
                        .status(
                                EnrollmentStatus.ACTIVE
                        )
                        .build();

        when(enrollmentService.enrollStudent(
                any(EnrollmentRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                post("/api/v1/enrollments")
                        .header(
                                "Authorization",
                                "Bearer " + adminToken
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        request
                                )
                        )
        )
        .andExpect(status().isCreated());
    }

    // =========================================================
    // ADMIN DASHBOARD
    // =========================================================

    @Test
    void adminShouldBeAllowedToAccessDashboard()
            throws Exception {

        when(studentRepository.count())
                .thenReturn(2L);

        when(courseRepository.count())
                .thenReturn(2L);

        when(enrollmentRepository.count())
                .thenReturn(2L);

        when(userRepository.countByStatus("PENDING"))
                .thenReturn(0L);

        mockMvc.perform(
                get("/api/v1/dashboard/stats")
                        .header(
                                "Authorization",
                                "Bearer " + adminToken
                        )
        )
        .andExpect(status().isOk());
    }

    // =========================================================
    // ADMIN USER MANAGEMENT
    // =========================================================

    @Test
    void adminShouldBeAllowedToAccessPendingUsers()
            throws Exception {

        when(userRegistrationService.getPendingUsers())
                .thenReturn(List.<UserResponse>of());

        mockMvc.perform(
                get("/api/v1/admin/users/pending")
                        .header(
                                "Authorization",
                                "Bearer " + adminToken
                        )
        )
        .andExpect(status().isOk());
    }

    @Test
    void adminShouldBeAllowedToApproveUser()
            throws Exception {

        when(userRegistrationService.approve(1L))
                .thenReturn(null);

        mockMvc.perform(
                put("/api/v1/admin/users/1/approve")
                        .header(
                                "Authorization",
                                "Bearer " + adminToken
                        )
        )
        .andExpect(status().isOk());
    }

    @Test
    void adminShouldBeAllowedToRejectUser()
            throws Exception {

        when(userRegistrationService.reject(1L))
                .thenReturn(null);

        mockMvc.perform(
                put("/api/v1/admin/users/1/reject")
                        .header(
                                "Authorization",
                                "Bearer " + adminToken
                        )
        )
        .andExpect(status().isOk());
    }
}