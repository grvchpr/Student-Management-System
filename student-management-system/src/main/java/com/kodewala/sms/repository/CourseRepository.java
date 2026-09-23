package com.kodewala.sms.repository;

import com.kodewala.sms.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByCourseCode(String courseCode);

    boolean existsByCourseCodeAndIdNot(
            String courseCode,
            Long id
    );

    Page<Course> findByCourseNameContainingIgnoreCase(
            String courseName,
            Pageable pageable
    );
}