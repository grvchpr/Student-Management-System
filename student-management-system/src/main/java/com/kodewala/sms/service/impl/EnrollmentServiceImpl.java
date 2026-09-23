package com.kodewala.sms.service.impl;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.kodewala.sms.service.EnrollmentService;



@Service
public class EnrollmentServiceImpl
        implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentServiceImpl(
            EnrollmentRepository enrollmentRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository) {

        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    @Override
    public EnrollmentResponse enrollStudent(
            EnrollmentRequest request) {

        Student student = studentRepository
                .findById(request.getStudentId())
                .orElseThrow(() ->
                        new StudentNotFoundException(
                                "Student not found with id: "
                                        + request.getStudentId()
                        ));

        Course course = courseRepository
                .findById(request.getCourseId())
                .orElseThrow(() ->
                        new CourseNotFoundException(
                                "Course not found with id: "
                                        + request.getCourseId()
                        ));

        if (enrollmentRepository
                .existsByStudentIdAndCourseId(
                        request.getStudentId(),
                        request.getCourseId())) {

            throw new DuplicateEnrollmentException(
                    "Student is already enrolled in this course"
            );
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrollmentDate(
                        request.getEnrollmentDate() != null
                                ? request.getEnrollmentDate()
                                : LocalDate.now()
                )
                .status(
                        request.getStatus() != null
                                ? request.getStatus()
                                : EnrollmentStatus.ACTIVE
                )
                .build();

        Enrollment savedEnrollment =
                enrollmentRepository.save(enrollment);

        return mapToResponse(savedEnrollment);
    }

    @Override
    public EnrollmentResponse getEnrollmentById(Long id) {

        Enrollment enrollment =
                enrollmentRepository.findById(id)
                        .orElseThrow(() ->
                                new EnrollmentNotFoundException(
                                        "Enrollment not found with id: "
                                                + id
                                ));

        return mapToResponse(enrollment);
    }

    @Override
    public Page<EnrollmentResponse> getAllEnrollments(
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return enrollmentRepository
                .findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<EnrollmentResponse> getEnrollmentsByStudent(
            Long studentId,
            int page,
            int size) {

        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException(
                    "Student not found with id: "
                            + studentId
            );
        }

        Pageable pageable =
                PageRequest.of(page, size);

        return enrollmentRepository
                .findByStudentId(studentId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<EnrollmentResponse> getEnrollmentsByCourse(
            Long courseId,
            int page,
            int size) {

        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException(
                    "Course not found with id: "
                            + courseId
            );
        }

        Pageable pageable =
                PageRequest.of(page, size);

        return enrollmentRepository
                .findByCourseId(courseId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    @Override
    public EnrollmentResponse updateEnrollmentStatus(
            Long id,
            EnrollmentStatus status) {

        Enrollment enrollment =
                enrollmentRepository.findById(id)
                        .orElseThrow(() ->
                                new EnrollmentNotFoundException(
                                        "Enrollment not found with id: "
                                                + id
                                ));

        enrollment.setStatus(status);

        Enrollment updatedEnrollment =
                enrollmentRepository.save(enrollment);

        return mapToResponse(updatedEnrollment);
    }

    @Override
    public void deleteEnrollment(Long id) {

        Enrollment enrollment =
                enrollmentRepository.findById(id)
                        .orElseThrow(() ->
                                new EnrollmentNotFoundException(
                                        "Enrollment not found with id: "
                                                + id
                                ));

        enrollmentRepository.delete(enrollment);
    }

    private EnrollmentResponse mapToResponse(
            Enrollment enrollment) {

        Student student = enrollment.getStudent();
        Course course = enrollment.getCourse();

        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .studentId(student.getId())
                .studentName(
                        student.getFirstName()
                                + " "
                                + student.getLastName()
                )
                .courseId(course.getId())
                .courseName(course.getCourseName())
                .courseCode(course.getCourseCode())
                .enrollmentDate(
                        enrollment.getEnrollmentDate()
                )
                .status(enrollment.getStatus())
                .createdAt(enrollment.getCreatedAt())
                .updatedAt(enrollment.getUpdatedAt())
                .build();
    }
}