package com.kodewala.sms.service;

import org.springframework.data.domain.Page;

import com.kodewala.sms.dto.StudentRequest;
import com.kodewala.sms.dto.StudentResponse;

public interface StudentService {

    StudentResponse createStudent(StudentRequest request);

    StudentResponse getStudentById(Long id);

    Page<StudentResponse> getAllStudents(
            int page,
            int size,
            String sortBy,
            String direction
    );

    StudentResponse updateStudent(
            Long id,
            StudentRequest request
    );

    void deleteStudent(Long id);
    
    Page<StudentResponse> searchByName(
            String name,
            int page,
            int size
    );
}