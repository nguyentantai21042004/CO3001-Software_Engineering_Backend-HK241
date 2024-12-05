package com.project.backend.services.report;

import com.project.backend.models.Report;

import java.util.List;

public interface IReportService {
    List<Report> getReport(Integer year) throws Exception;
}
