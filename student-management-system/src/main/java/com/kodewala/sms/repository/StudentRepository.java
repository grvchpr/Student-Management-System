package com.kodewala.sms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kodewala.sms.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Page<Student> findByFirstNameContainingIgnoreCase(
            String firstName,
            Pageable pageable
    );

    Page<Student> findByLastNameContainingIgnoreCase(
            String lastName,
            Pageable pageable
    );

    Page<Student> findByEmailContainingIgnoreCase(
            String email,
            Pageable pageable
    );
}