package com.project.backend.services.report;

import com.google.firebase.database.core.Repo;
import com.project.backend.models.Report;
import com.project.backend.repositories.ReportRepository;
import com.project.backend.responses.report.ReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService implements IReportService{
    private final ReportRepository reportRepository;
    @Override
    public List<Report> getReport(Integer year) throws Exception{
        List<Report> reportList = new ArrayList<>();
        for(int i = 1; i <= 12; i++){
            reportList.add(i - 1, reportRepository.getMonthlyReport(i, year, "monthly"));
        }
        reportList.add(12, reportRepository.getYearlyReport(year, "yearly"));

        return reportList;
    }
}
