package com.kodewala.sms.repository;

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

    Page<Enrollment> findByStudentId(
            Long studentId,
            Pageable pageable
    );

    Page<Enrollment> findByCourseId(
            Long courseId,
            Pageable pageable
    );
    
    @EntityGraph(attributePaths = {
            "student",
            "course"
    })
    Page<Enrollment> findAll(Pageable pageable);
    
    boolean existsByStudentId(Long studentId);

    boolean existsByCourseId(Long courseId);
}