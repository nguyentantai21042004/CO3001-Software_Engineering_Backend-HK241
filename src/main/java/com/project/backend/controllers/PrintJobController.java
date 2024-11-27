package com.project.backend.controllers;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.dataTranferObjects.PrintJobCreateDTO;
import com.project.backend.exceptions.BalanceException;
import com.project.backend.exceptions.InvalidAccessException;
import com.project.backend.exceptions.JWTException;
import com.project.backend.models.File;
import com.project.backend.models.PrintJob;
import com.project.backend.models.Printer;
import com.project.backend.models.SPSO;
import com.project.backend.models.Student;
import com.project.backend.responses.ResponseObject;
import com.project.backend.responses.printjob.PrintJobDetailResponse;
import com.project.backend.responses.printjob.PrintJobListResponse;
import com.project.backend.responses.printjob.PrintJobResponse;
import com.project.backend.services.printjob.IPrintJobService;
import com.project.backend.services.admin.IAdminService;
import com.project.backend.services.file.IFileService;
import com.project.backend.services.printer.IPrinterService;
import com.project.backend.services.student.IStudentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.prefix}/print_jobs")
@RequiredArgsConstructor
public class PrintJobController {
    private final IPrintJobService printJobService;
    private final IStudentService studentService;
    private final IPrinterService printerService;
    private final IFileService fileService;
    private final IAdminService adminService;

    @GetMapping("/get")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('SPSO')")
    public ResponseEntity<ResponseObject> Get(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestHeader("Authorization") String authorizationHeader)
            throws Exception {
        if (page < 1 || limit < 1) {
            throw new BadRequestException("Page and limit must be greater than 0");
        }

        PageRequest pageRequest = PageRequest.of(
                page - 1, limit,
                Sort.by("id").ascending());

        try {
            String extractedToken = authorizationHeader.substring(7);
            try {
                Student student = studentService.getDetailFromToken(extractedToken);
                if (student != null) {
                    Page<PrintJob> printJobs = printJobService.Get(pageRequest, keyword, student.getStudentId());
                    Page<PrintJobResponse> printJobResponses = printJobs.map(PrintJobResponse::fromPrintJob);

                    int totalPages = printJobs.getTotalPages();

                    PrintJobListResponse printJobListResponse = PrintJobListResponse.builder()
                            .printJobs(printJobResponses.getContent())
                            .currentPage(page)
                            .itemsPerPage(limit)
                            .totalPages(totalPages)
                            .build();

                    return ResponseEntity.ok(ResponseObject.builder()
                            .data(printJobListResponse)
                            .message("Print job list")
                            .status(HttpStatus.OK)
                            .build());
                }
            } catch (Exception e) {
                SPSO spso = adminService.getUserDetailsFromToken(extractedToken);
                if (spso != null) {
                    Page<PrintJob> printJobs = printJobService.Get(pageRequest, keyword);
                    Page<PrintJobResponse> printJobResponses = printJobs.map(PrintJobResponse::fromPrintJob);

                    int totalPages = printJobs.getTotalPages();

                    PrintJobListResponse printJobListResponse = PrintJobListResponse.builder()
                            .printJobs(printJobResponses.getContent())
                            .currentPage(page)
                            .itemsPerPage(limit)
                            .totalPages(totalPages)
                            .build();

                    return ResponseEntity.ok(ResponseObject.builder()
                            .data(printJobListResponse)
                            .message("Print job list")
                            .status(HttpStatus.OK)
                            .build());
                }
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseObject.builder()
                    .message("Unauthorized")
                    .status(HttpStatus.UNAUTHORIZED)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data(null)
                    .build());
        }
    }

    @GetMapping("/{print_job_id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> Detail(@PathVariable("print_job_id") Integer printJobId,
            @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        try {
            String extractedToken = authorizationHeader.substring(7);
            Student student = studentService.getDetailFromToken(extractedToken);

            PrintJob printJob = printJobService.Detail(printJobId, student.getStudentId());
            Printer printer = printerService.Detail(printJob.getPrinterId());

            File file = fileService.getFileById(printJob.getFileId());
            return ResponseEntity
                    .ok(ResponseObject.builder()
                            .data(PrintJobDetailResponse.fromPrintJob(printJob, printer, file, student))
                            .message("Print job detail")
                            .status(HttpStatus.OK)
                            .build());
        } catch (JWTException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.UNAUTHORIZED)
                    .data(null)
                    .build());
        } catch (InvalidAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.FORBIDDEN)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data(null)
                    .build());
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> Create(
            @RequestBody PrintJobCreateDTO printJobCreateDTO,
            BindingResult result,
            @RequestHeader("Authorization") String authorizationHeader) throws Exception {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(result.getFieldError().getDefaultMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        }

        try {
            String extractedToken = authorizationHeader.substring(7);
            Student student = studentService.getDetailFromToken(extractedToken);

            PrintJob newPrintJob = printJobService.Create(printJobCreateDTO, student.getStudentId());

            Printer printer = printerService.Detail(newPrintJob.getPrinterId());

            File file = fileService.getFileById(newPrintJob.getFileId());

            return ResponseEntity.ok(ResponseObject.builder()
                    .data(PrintJobDetailResponse.fromPrintJob(newPrintJob, printer, file, student))
                    .message("Print job created successfully")
                    .status(HttpStatus.OK)
                    .build());
        } catch (BalanceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .data(null)
                    .build());
        }
    }
}
