package com.project.backend.repositories;

import com.project.backend.models.PageAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PageAllocationRepository extends JpaRepository<PageAllocation, Long> {
    List<PageAllocation> findByYear(short year);
}
