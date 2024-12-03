package com.project.backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "spso_id")
    private Integer spsoId;

    @Column(name = "paper_purchase_count")
    private Integer paperPurchaseCount;

    @Column(name = "paper_sold_count")
    private Integer paperSoldCount;

    @Column(name = "revenue_from_paper_sales")
    private Integer revenueFromPaperSales;

    @Column(name = "printed_a4_pages")
    private Integer printedA4Pages;

    @Column(name = "printed_a3_pages")
    private Integer printedA3Pages;

    @Column(name = "print_job_count")
    private Integer printJobCount;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "type")
    private String type;
}
