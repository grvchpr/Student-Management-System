package com.kodewala.sms.service.impl;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kodewala.sms.dto.StudentRequest;
import com.kodewala.sms.dto.StudentResponse;
import com.kodewala.sms.entity.Student;
import com.kodewala.sms.exception.DuplicateEmailException;
import com.kodewala.sms.exception.StudentNotFoundException;
import com.kodewala.sms.repository.StudentRepository;
import com.kodewala.sms.service.StudentService;
import com.kodewala.sms.util.PageRequestUtil;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public StudentResponse createStudent(StudentRequest request) {

        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(
                    "Student already exists with email: "
                            + request.getEmail()
            );
        }

        Student student = new Student();

        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setAddress(request.getAddress());

        Student savedStudent = studentRepository.save(student);

        return mapToResponse(savedStudent);
    }

    @Override
    public StudentResponse getStudentById(Long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException(
                                "Student not found with id: " + id
                        ));

        return mapToResponse(student);
    }

    @Override
    public Page<StudentResponse> getAllStudents(
            int page,
            int size,
            String sortBy,
            String direction) {

        Set<String> allowedSortFields = Set.of(
                "id",
                "firstName",
                "lastName",
                "email",
                "dateOfBirth",
                "createdAt"
        );

        Pageable pageable = PageRequestUtil.create(
                page,
                size,
                sortBy,
                direction,
                allowedSortFields
        );

        return studentRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public StudentResponse updateStudent(
            Long id,
            StudentRequest request) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException(
                                "Student not found with id: " + id
                        ));

        if (studentRepository.existsByEmailAndIdNot(
                request.getEmail(),
                id)) {

            throw new DuplicateEmailException(
                    "Another student already exists with email: "
                            + request.getEmail()
            );
        }

        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setAddress(request.getAddress());

        Student updatedStudent =
                studentRepository.save(student);

        return mapToResponse(updatedStudent);
    }

    @Override
    public void deleteStudent(Long id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new StudentNotFoundException(
                                "Student not found with id: " + id
                        ));

        studentRepository.delete(student);
    }

    private StudentResponse mapToResponse(Student student) {

        return StudentResponse.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .phone(student.getPhone())
                .dateOfBirth(student.getDateOfBirth())
                .address(student.getAddress())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }

    @Override
    public Page<StudentResponse> searchByName(
            String name,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("firstName").ascending()
        );

        return studentRepository
                .findByFirstNameContainingIgnoreCase(
                        name,
                        pageable
                )
                .map(this::mapToResponse);
    }
    
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of(
                    "id",
                    "firstName",
                    "lastName",
                    "email",
                    "dateOfBirth",
                    "createdAt"
            );
}