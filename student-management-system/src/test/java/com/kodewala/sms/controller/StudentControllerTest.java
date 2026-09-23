package com.kodewala.sms.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kodewala.sms.dto.StudentRequest;
import com.kodewala.sms.dto.StudentResponse;
import com.kodewala.sms.service.StudentService;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @MockitoBean
    private StudentService studentService;

    // =========================================================
    // CREATE STUDENT
    // =========================================================

    @Test
    void createStudent_shouldReturn201() throws Exception {

        StudentRequest request = StudentRequest.builder()
                .firstName("Gourav")
                .lastName("Chopra")
                .email("gourav@gmail.com")
                .phone("9876543210")
                .dateOfBirth(LocalDate.of(1996, 12, 27))
                .address("Bangalore")
                .build();

        StudentResponse response = StudentResponse.builder()
                .id(1L)
                .firstName("Gourav")
                .lastName("Chopra")
                .email("gourav@gmail.com")
                .phone("9876543210")
                .dateOfBirth(LocalDate.of(1996, 12, 27))
                .address("Bangalore")
                .build();

        when(studentService.createStudent(any(StudentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/students")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Gourav"))
                .andExpect(jsonPath("$.lastName").value("Chopra"))
                .andExpect(jsonPath("$.email")
                        .value("gourav@gmail.com"));

        verify(studentService)
                .createStudent(any(StudentRequest.class));
    }

    // =========================================================
    // GET STUDENT BY ID
    // =========================================================

    @Test
    void getStudentById_shouldReturn200() throws Exception {

        StudentResponse response = StudentResponse.builder()
                .id(1L)
                .firstName("Gourav")
                .lastName("Chopra")
                .email("gourav@gmail.com")
                .build();

        when(studentService.getStudentById(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/students/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName")
                        .value("Gourav"))
                .andExpect(jsonPath("$.email")
                        .value("gourav@gmail.com"));

        verify(studentService)
                .getStudentById(1L);
    }

    // =========================================================
    // GET ALL STUDENTS
    // =========================================================

    @Test
    void getAllStudents_shouldReturn200() throws Exception {

        StudentResponse response = StudentResponse.builder()
                .id(1L)
                .firstName("Gourav")
                .lastName("Chopra")
                .email("gourav@gmail.com")
                .build();

        PageImpl<StudentResponse> page =
                new PageImpl<>(
                        List.of(response),
                        PageRequest.of(0, 10),
                        1
                );

        when(studentService.getAllStudents(
                eq(0),
                eq(10),
                eq("id"),
                eq("asc")
        )).thenReturn(page);

        mockMvc.perform(
                        get("/api/v1/students")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id")
                        .value(1))
                .andExpect(jsonPath("$.content[0].firstName")
                        .value("Gourav"));

        verify(studentService)
                .getAllStudents(0, 10, "id", "asc");
    }

    // =========================================================
    // SEARCH STUDENTS
    // =========================================================

    @Test
    void searchStudents_shouldReturn200() throws Exception {

        StudentResponse response = StudentResponse.builder()
                .id(1L)
                .firstName("Gourav")
                .lastName("Chopra")
                .email("gourav@gmail.com")
                .build();

        PageImpl<StudentResponse> page =
                new PageImpl<>(
                        List.of(response),
                        PageRequest.of(0, 10),
                        1
                );

        when(studentService.searchByName(
                eq("Gourav"),
                eq(0),
                eq(10)
        )).thenReturn(page);

        mockMvc.perform(
                        get("/api/v1/students/search")
                                .param("name", "Gourav")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName")
                        .value("Gourav"));

        verify(studentService)
                .searchByName("Gourav", 0, 10);
    }

    // =========================================================
    // UPDATE STUDENT
    // =========================================================

    @Test
    void updateStudent_shouldReturn200() throws Exception {

        StudentRequest request = StudentRequest.builder()
                .firstName("Gourav")
                .lastName("Chopra")
                .email("gourav@gmail.com")
                .phone("9876543210")
                .dateOfBirth(LocalDate.of(1996, 12, 27))
                .address("Bangalore")
                .build();

        StudentResponse response = StudentResponse.builder()
                .id(1L)
                .firstName("Gourav")
                .lastName("Chopra")
                .email("gourav@gmail.com")
                .build();

        when(studentService.updateStudent(
                eq(1L),
                any(StudentRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/v1/students/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName")
                        .value("Gourav"));

        verify(studentService)
                .updateStudent(
                        eq(1L),
                        any(StudentRequest.class)
                );
    }

    // =========================================================
    // DELETE STUDENT
    // =========================================================

    @Test
    void deleteStudent_shouldReturn204() throws Exception {

        doNothing()
                .when(studentService)
                .deleteStudent(1L);

        mockMvc.perform(
                        delete("/api/v1/students/1")
                )
                .andExpect(status().isNoContent());

        verify(studentService)
                .deleteStudent(1L);
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    @Test
    void createStudent_shouldReturn400WhenEmailIsInvalid()
            throws Exception {

        StudentRequest request = StudentRequest.builder()
                .firstName("Gourav")
                .lastName("Chopra")
                .email("invalid-email")
                .phone("9876543210")
                .build();

        mockMvc.perform(
                        post("/api/v1/students")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());

        verify(studentService, never())
                .createStudent(any(StudentRequest.class));
    }
}