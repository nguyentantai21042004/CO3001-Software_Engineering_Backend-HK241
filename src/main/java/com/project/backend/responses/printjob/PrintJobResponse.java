package com.project.backend.responses.printjob;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.backend.models.PrintJob;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrintJobResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("status")
    private String status;

    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    public static PrintJobResponse fromPrintJob(PrintJob printJob) {
        return PrintJobResponse.builder()
                .id(Long.valueOf(printJob.getId()))
                .status(printJob.getStatus())
                .date(printJob.getDate())
                .build();
    }
}
