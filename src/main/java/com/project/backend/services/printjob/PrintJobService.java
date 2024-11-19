package com.project.backend.services.printjob;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.backend.dataTranferObjects.PrintJobCreateDTO;
import com.project.backend.models.File;
import com.project.backend.models.PrintJob;
import com.project.backend.models.Printer;
import com.project.backend.models.Student;
import com.project.backend.repositories.FileRepository;
import com.project.backend.repositories.PrintJobRepository;
import com.project.backend.repositories.PrinterRepository;
import com.project.backend.repositories.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrintJobService implements IPrintJobService {
    private final PrintJobRepository printJobRepository;
    private final FileRepository fileRepository;
    private final PrinterRepository printerRepository;
    private final StudentRepository studentRepository;

    @Override
    public PrintJob createPrintJob(PrintJobCreateDTO printJobCreateDTO, Integer studentId) throws Exception {
        Optional<File> existingFile = fileRepository.findById(printJobCreateDTO.getFileId());
        if (!existingFile.isPresent()) {
            throw new Exception("File not found");
        }

        Optional<Printer> existingPrinter = printerRepository.findById(printJobCreateDTO.getPrinterId());
        if (!existingPrinter.isPresent()) {
            throw new Exception("Printer not found");
        }

        Optional<Student> existingStudent = studentRepository.findById(studentId.longValue());
        if (!existingStudent.isPresent()) {
            throw new Exception("Student not found");
        }
        PrintJob printJob = PrintJob.builder()
                .fileId(1) // ID của file hợp lệ
                .printerId(1) // ID của máy in hợp lệ
                .pageNumber("1") // Số trang in (giả định in 1 trang)
                .studentId(1) // ID của sinh viên hợp lệ
                .pageSize("A4") // Kích thước giấy (A4 hoặc A3)
                .numberOfCopies(1) // Số bản in (giả định in 1 bản)
                .colorMode(false) // Chế độ in đen trắng
                .pageScale(100) // Tỷ lệ in 100%
                .pageSide("one sided") // In một mặt
                .isDuplex(false) // Không in hai mặt
                .status("successful") // Trạng thái ban đầu
                .date(LocalDateTime.now()) // Ngày tạo lệnh in
                .totalPage(1) // Tổng số trang cần in
                .totalCost(1000) // Chi phí cho lệnh in
                .build();

        return printJobRepository.save(printJob);
    }
}
