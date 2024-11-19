package com.project.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.backend.models.PrintJob;

public interface PrintJobRepository extends JpaRepository<PrintJob, Long> {

}
