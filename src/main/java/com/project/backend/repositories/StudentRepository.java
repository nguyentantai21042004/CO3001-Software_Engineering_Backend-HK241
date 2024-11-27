package com.project.backend.repositories;

import com.project.backend.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);

    Optional<Student> findById(Long id);

    @Query("SELECT s FROM Student s WHERE :keyword IS NULL OR s.email LIKE %:keyword%")
    Page<Student> findAll(PageRequest pageRequest, String keyword);
}
