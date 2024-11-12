package com.project.backend.repositories;

import com.project.backend.models.Printer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrinterRepository extends JpaRepository<Printer, Integer> {
    // Custom queries to find printers based on location and SPSO ID
    List<Printer> findByLocationId(Integer locationId);

    List<Printer> findBySpsoId(Integer spsoId);
}
