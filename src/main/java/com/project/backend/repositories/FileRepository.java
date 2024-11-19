package com.project.backend.repositories;

import com.project.backend.models.File;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findById(Long id);
}
