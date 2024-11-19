package com.project.backend.repositories;

import com.project.backend.models.Printer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrinterRepository extends JpaRepository<Printer, Integer> {
    // Custom queries to find printers based on location and SPSO ID
    List<Printer> findByLocationId(Integer locationId);

    Optional<Printer> findById(Long id);

    List<Printer> findBySpsoId(Integer spsoId);
}
