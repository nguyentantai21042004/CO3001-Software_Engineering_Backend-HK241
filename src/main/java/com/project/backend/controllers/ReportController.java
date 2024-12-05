package com.project.backend.controllers;

import com.project.backend.models.Report;
import com.project.backend.responses.ResponseObject;
import com.project.backend.responses.report.ReportResponse;
import com.project.backend.services.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/{year}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SPSO')")
    public ResponseEntity<ResponseObject> getReport(@PathVariable Integer year) throws Exception {
        List<Report> reportList = reportService.getReport(year);

        List<ReportResponse> reportResponseList = reportList.stream()
                .map(ReportResponse::fromReport)
                .toList();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get report successfully")
                .status(HttpStatus.OK)
                .data(reportResponseList)
                .build());
    }
}
