package com.project.backend.responses.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.models.Report;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportResponse {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("spso_id")
    private Integer spsoId;

    @JsonProperty("paper_purchase_count")
    private Integer paperPurchaseCount;

    @JsonProperty("paper_sold_count")
    private Integer paperSoldCount;

    @JsonProperty("revenue_prom_paper_sales")
    private Integer revenueFromPaperSales;

    @JsonProperty("printed_a4_pages")
    private Integer printedA4Pages;

    @JsonProperty("printed_a3_pages")
    private Integer printedA3Pages;

    @JsonProperty("print_job_count")
    private Integer printJobCount;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("type")
    private String type;

    public static ReportResponse fromReport(Report report) {
        if (report == null)
            return null;
        return ReportResponse.builder()
                .id(report.getId())
                .spsoId(report.getSpsoId())
                .paperPurchaseCount(report.getPaperPurchaseCount())
                .paperSoldCount(report.getPaperSoldCount())
                .revenueFromPaperSales(report.getRevenueFromPaperSales())
                .printedA4Pages(report.getPrintedA4Pages())
                .printedA3Pages(report.getPrintedA3Pages())
                .printJobCount(report.getPrintJobCount())
                .startDate(report.getStartDate())
                .endDate(report.getEndDate())
                .type(report.getType())
                .build();
    }
}
