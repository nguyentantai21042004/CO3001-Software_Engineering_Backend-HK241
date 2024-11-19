package com.project.backend.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.models.PrintJob;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrintJobResponse {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("file_id")
    private Integer fileId;

    @JsonProperty("file_type")
    private String fileType;

    @JsonProperty("file_size")
    private Long fileSize;

    @JsonProperty("printer_id")
    private Integer printerId;

    @JsonProperty("student_id")
    private Integer studentId;

    @JsonProperty("page_size")
    private String pageSize;

    @JsonProperty("number_of_copies")
    private Integer numberOfCopies;

    @JsonProperty("is_duplex")
    private Boolean isDuplex;

    @JsonProperty("special_notes")
    private String specialNotes;

    public static PrintJobResponse fromPrintJob(PrintJob printJob) {
        return PrintJobResponse.builder()
                .id(printJob.getId())
                .fileId(printJob.getFileId())
                .printerId(printJob.getPrinterId())
                .studentId(printJob.getStudentId())
                .pageSize(printJob.getPageSize())
                .numberOfCopies(printJob.getNumberOfCopies())
                .isDuplex(printJob.getIsDuplex())
                .build();
    }
}
