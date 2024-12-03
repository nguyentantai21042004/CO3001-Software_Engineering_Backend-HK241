package com.project.backend.services.report;

import com.project.backend.models.Report;
import com.project.backend.responses.report.ReportResponse;

import java.util.List;

public interface IReportService {
    List<Report> getReport(Integer year) throws Exception;
}
