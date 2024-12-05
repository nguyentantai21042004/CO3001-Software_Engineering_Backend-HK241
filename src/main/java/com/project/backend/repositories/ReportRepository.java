package com.project.backend.repositories;

import com.project.backend.models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    @Query(value = "SELECT * FROM reports WHERE YEAR(start_date) = :year AND MONTH(start_date) = :month AND type = :type", nativeQuery = true)
    Report getMonthlyReport(Integer month, Integer year, String type);

    @Query(value = "SELECT * FROM reports WHERE YEAR(start_date) = :year AND type = :type", nativeQuery = true)
    Report getYearlyReport(Integer year, String type);
}
