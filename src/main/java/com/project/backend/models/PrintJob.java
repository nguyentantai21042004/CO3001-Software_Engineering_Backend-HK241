package com.project.backend.models;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "print_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrintJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "file_id", nullable = false)
    private Integer fileId;

    @Column(name = "number_of_copies", nullable = false)
    private Integer numberOfCopies;

    @Column(name = "page_number", columnDefinition = "TEXT")
    private String pageNumber;

    @Column(name = "is_duplex", nullable = false)
    private Boolean isDuplex;

    @Column(name = "page_side", length = 25)
    private String pageSide;

    @Column(name = "page_size", nullable = false, length = 2)
    private String pageSize;

    @Column(name = "page_scale")
    private Integer pageScale;

    @Column(name = "color_mode")
    private Boolean colorMode;

    @Column(name = "submission_time")
    private LocalDateTime submissionTime;

    @Column(name = "completion_time")
    private LocalDateTime completionTime;

    @Column(name = "status", length = 15)
    private String status;

    @Column(name = "date", columnDefinition = "DATETIME DEFAULT NOW()")
    private LocalDateTime date;

    @Column(name = "total_page")
    private Integer totalPage;

    @Column(name = "total_cost")
    private Integer totalCost;

    @Column(name = "printer_id", nullable = false)
    private Integer printerId;
}
