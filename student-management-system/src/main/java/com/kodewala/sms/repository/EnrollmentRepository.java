package com.kodewala.sms.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kodewala.sms.entity.Enrollment;

public interface EnrollmentRepository
        extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentIdAndCourseId(
            Long studentId,
            Long courseId
    );

    @Override
    @EntityGraph(attributePaths = {
            "student",
            "course"
    })
    Optional<Enrollment> findById(Long id);

    @EntityGraph(attributePaths = {
            "student",
            "course"
    })
    Page<Enrollment> findByStudentId(
            Long studentId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "student",
            "course"
    })
    Page<Enrollment> findByCourseId(
            Long courseId,
            Pageable pageable
    );

    @Override
    @EntityGraph(attributePaths = {
            "student",
            "course"
    })
    Page<Enrollment> findAll(Pageable pageable);
    
    boolean existsByStudentId(Long studentId);
    
    boolean existsByCourseId(Long courseId);
}