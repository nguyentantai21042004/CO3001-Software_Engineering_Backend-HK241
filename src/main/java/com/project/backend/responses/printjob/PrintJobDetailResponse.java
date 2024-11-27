package com.project.backend.responses.printjob;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.backend.models.File;
import com.project.backend.models.PrintJob;
import com.project.backend.models.Printer;
import com.project.backend.models.Student;
import com.project.backend.responses.file.FileResponse;
import com.project.backend.responses.printer.PrinterResponse;
import com.project.backend.responses.students.StudentResponse;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrintJobDetailResponse {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("file")
    private FileResponse file;

    @JsonProperty("page_size")
    private String pageSize;

    @JsonProperty("number_of_copies")
    private Integer numberOfCopies;

    @JsonProperty("is_duplex")
    private Boolean isDuplex;

    @JsonProperty("status")
    private String status;

    @JsonProperty("printer")
    private PrinterResponse printer;

    @JsonProperty("student")
    private StudentResponse student;

    @JsonProperty("submission_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submissionTime;

    @JsonProperty("completed_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedTime;

    public static PrintJobDetailResponse fromPrintJob(PrintJob printJob, Printer printer, File file, Student student) {
        return PrintJobDetailResponse.builder()
                .id(printJob.getId())
                .file(FileResponse.fromFile(file))
                .printer(PrinterResponse.fromPrinter(printer))
                .pageSize(printJob.getPageSize())
                .submissionTime(printJob.getSubmissionTime())
                .completedTime(printJob.getCompletionTime())
                .status(printJob.getStatus().toString())
                .numberOfCopies(printJob.getNumberOfCopies())
                .isDuplex(printJob.getIsDuplex())
                .student(StudentResponse.fromStudent(student))
                .build();
    }
}
