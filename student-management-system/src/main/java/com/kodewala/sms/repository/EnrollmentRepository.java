package com.kodewala.sms.repository;

import com.kodewala.sms.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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
}