package com.project.backend.repositories;

import com.project.backend.models.FileFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileFormatRepository extends JpaRepository<FileFormat, Integer> {
    boolean existsByName(String name);
    Optional<FileFormat> findByName(String name);
}
