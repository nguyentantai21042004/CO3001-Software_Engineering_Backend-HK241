package com.project.backend.repositories;

import com.project.backend.models.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {
    @Query(value = "SELECT * FROM files WHERE student_id = :studentId", nativeQuery = true)
    Page<File> findAllFilesByStudentId(Integer studentId, Pageable pageable);

    Optional<File> findById(Integer id);
}
