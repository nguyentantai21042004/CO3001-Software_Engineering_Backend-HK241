package com.project.backend.services.printjob;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.backend.dataTranferObjects.PrintJobCreateDTO;
import com.project.backend.exceptions.BalanceException;
import com.project.backend.exceptions.DataNotFoundException;
import com.project.backend.exceptions.InvalidAccessException;
import com.project.backend.models.File;
import com.project.backend.models.PrintJob;
import com.project.backend.models.Printer;
import com.project.backend.models.Student;
import com.project.backend.repositories.FileRepository;
import com.project.backend.repositories.PrintJobRepository;
import com.project.backend.repositories.PrinterRepository;
import com.project.backend.repositories.StudentRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrintJobService implements IPrintJobService {
    private final PrintJobRepository printJobRepository;
    private final FileRepository fileRepository;
    private final PrinterRepository printerRepository;
    private final StudentRepository studentRepository;

    @Override
    public PrintJob Create(PrintJobCreateDTO printJobCreateDTO, Integer studentId) throws Exception {
        Optional<File> existingFile = fileRepository.findById(printJobCreateDTO.getFileId());
        if (!existingFile.isPresent()) {
            throw new Exception("File not found");
        }

        Optional<Printer> existingPrinter = printerRepository.findById(printJobCreateDTO.getPrinterId());
        if (!existingPrinter.isPresent()) {
            throw new Exception("Printer not found");
        }
        Printer printer = existingPrinter.get();
        if ("inactive".equals(printer.getStatus())) {
            throw new Exception("Printer is inactive");
        }

        Optional<Student> existingStudent = studentRepository.findById(studentId.longValue());
        if (!existingStudent.isPresent()) {
            throw new Exception("Student not found");
        }

        if ("occupied".equals(printer.getStatus())) {
            throw new Exception("Printer is currently occupied");
        }

        if (!"A4".equals(printJobCreateDTO.getPageSize()) && !"A3".equals(printJobCreateDTO.getPageSize())) {
            throw new Exception("Invalid page size. Only A4 or A3 are supported.");
        }

        if (!"one sided".equals(printJobCreateDTO.getPageSide())
                && !"double sided".equals(printJobCreateDTO.getPageSide())) {
            throw new Exception("Invalid page side. Only 'one sided' or 'double sided' are supported.");
        }

        if (!printJobCreateDTO.getPageNumber().matches("^\\d+(,\\d+)*$")) {
            throw new Exception("Invalid page number format. Use format like '1,2,3'.");
        }

        if (printJobCreateDTO.getNumberOfCopies() == null || printJobCreateDTO.getNumberOfCopies() <= 0) {
            throw new Exception("Number of copies must be greater than 0.");
        }

        if (printJobCreateDTO.getColorMode() == null) {
            printJobCreateDTO.setColorMode(false);
        }

        Student student = existingStudent.get();
        int totalPage = calculateTotalPages(printJobCreateDTO.getPageNumber(),
                "double sided".equals(printJobCreateDTO.getPageSide()));
        int costPerPage = "A4".equals(printJobCreateDTO.getPageSize()) ? 1 : 2;
        int totalCost = totalPage * printJobCreateDTO.getNumberOfCopies() * costPerPage;

        if (student.getStudentBalance() < totalCost) {
            throw new BalanceException(
                    "Insufficient balance. Required: " + totalCost + ", Available: " + student.getStudentBalance());
        }

        PrintJob printJob = PrintJob.builder()
                .fileId(printJobCreateDTO.getFileId())
                .printerId(printJobCreateDTO.getPrinterId().intValue())
                .pageNumber(printJobCreateDTO.getPageNumber())
                .studentId(studentId)
                .pageSize(printJobCreateDTO.getPageSize())
                .numberOfCopies(printJobCreateDTO.getNumberOfCopies())
                .colorMode(printJobCreateDTO.getColorMode())
                .pageScale(100) // Default value
                .pageSide(printJobCreateDTO.getPageSide())
                .isDuplex(printJobCreateDTO.getIsDuplex())
                .status("in progress") // Default initial status
                .date(LocalDateTime.now())
                .submissionTime(LocalDateTime.now())
                .completionTime(LocalDateTime.now())
                .totalPage(totalPage)
                .totalCost(totalCost)
                .build();

        return printJobRepository.save(printJob);
    }

    @Override
    public PrintJob Detail(Integer printJobId, Integer studentId) throws Exception {
        Optional<Student> existingStudent = studentRepository.findById(Long.valueOf(studentId));
        if (!existingStudent.isPresent()) {
            throw new DataNotFoundException("Student not found");
        }

        Optional<PrintJob> existingPrintJob = printJobRepository.findById(Long.valueOf(printJobId));
        if (!existingPrintJob.isPresent()) {
            throw new DataNotFoundException("Print job not found");
        }

        PrintJob printJob = existingPrintJob.get();
        if (printJob.getStudentId() != studentId) {
            throw new InvalidAccessException("Invalid access");
        }

        return printJob;
    }

    @Override
    public Page<PrintJob> Get(PageRequest pageRequest, String keyword, Integer studentId) throws Exception {
        return printJobRepository.findByStudentId(studentId, pageRequest, keyword);
    }

    @Override
    public Page<PrintJob> Get(PageRequest pageRequest, String keyword) throws Exception {
        return printJobRepository.findAll(pageRequest, keyword);
    }

    private int calculateTotalPages(String pageNumber, boolean isDoubleSided) {
        int totalPages = pageNumber.split(",").length;

        if (isDoubleSided) {
            totalPages = (int) Math.ceil(totalPages / 2.0);
        }

        return totalPages;
    }
}
