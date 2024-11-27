package com.project.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.backend.models.PrintJob;

public interface PrintJobRepository extends JpaRepository<PrintJob, Long> {
    @Query("SELECT p FROM PrintJob p WHERE p.studentId = :studentId and :keyword IS NULL OR p.status LIKE %:keyword%")
    Page<PrintJob> findByStudentId(Integer studentId, Pageable pageable, String keyword);

    @Query("SELECT p FROM PrintJob p WHERE :keyword IS NULL OR p.status LIKE %:keyword%")
    Page<PrintJob> findAll(Pageable pageable, String keyword);
}
