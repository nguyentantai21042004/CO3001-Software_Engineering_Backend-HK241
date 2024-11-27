package com.project.backend.responses.printjob;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrintJobListResponse {
    @JsonProperty("print_jobs")
    private List<PrintJobResponse> printJobs;

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("items_per_page")
    private int itemsPerPage;

    @JsonProperty("total_pages")
    private int totalPages;
}
