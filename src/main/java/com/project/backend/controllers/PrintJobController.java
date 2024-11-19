package com.project.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.dataTranferObjects.PrintJobCreateDTO;
import com.project.backend.models.PrintJob;
import com.project.backend.models.Student;
import com.project.backend.responses.PrintJobResponse;
import com.project.backend.responses.ResponseObject;
import com.project.backend.services.printjob.IPrintJobService;
import com.project.backend.services.student.IStudentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/print-jobs")
@RequiredArgsConstructor
public class PrintJobController {
    private final IPrintJobService printJobService;
    private final IStudentService studentService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> createPrintJob(
            @RequestBody PrintJobCreateDTO printJobCreateDTO,
            BindingResult bindingResult,
            @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("printjob.create.failed")
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }

        String extractedToken = authorizationHeader.substring(7);
        Student student = studentService.getStudentDetailsByExtractingToken(extractedToken);

        PrintJob newPrintJob = printJobService.createPrintJob(printJobCreateDTO, student.getStudentId());

        return ResponseEntity.ok(ResponseObject.builder()
                .data(PrintJobResponse.fromPrintJob(newPrintJob))
                .message("Print job created successfully")
                .status(HttpStatus.OK)
                .build());
    }
}
