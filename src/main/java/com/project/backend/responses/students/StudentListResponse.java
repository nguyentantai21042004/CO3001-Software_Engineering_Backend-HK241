package com.project.backend.responses.students;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
public class StudentListResponse {
    @JsonProperty("students")
    private List<StudentResponse> students;

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("items_per_page")
    private int itemsPerPage;

    @JsonProperty("total_pages")
    private int totalPages;
}
