package com.project.backend.controllers;

import com.project.backend.models.File;
import com.project.backend.models.Student;
import com.project.backend.responses.ResponseObject;
import com.project.backend.responses.file.FileDetailResponse;
import com.project.backend.services.file.FileService;
import com.project.backend.services.student.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/files")
@RequiredArgsConstructor
public class FileController {
        private final FileService fileService;
        private final StudentService studentService;
        @PostMapping
        @PreAuthorize("hasRole('STUDENT')")
        public ResponseEntity<ResponseObject> uploadFile(@RequestParam String token, @RequestParam MultipartFile file)
                        throws Exception {
                File newFile = fileService.uploadFile(token, file);
                FileDetailResponse fileResponse = FileDetailResponse.builder()
                                .id(newFile.getId())
                                .size(newFile.getSize())
                                .name(newFile.getName())
                                .url(newFile.getUrl())
                                .uploadDate(newFile.getUploadDate())
                                .fileFormat(newFile.getFileFormat().getName())
                                .build();
                return ResponseEntity.ok().body(ResponseObject.builder()
                                .message("Upload file successfully")
                                .status(HttpStatus.CREATED)
                                .data(fileResponse)
                                .build());
        }

        @GetMapping("/get-all")
        @PreAuthorize("hasRole('STUDENT')")
        public ResponseEntity<ResponseObject> getAllFiles(
                        @RequestHeader("Authorization") String authorizationHeader,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int limit) throws Exception {
                if (page < 1)
                        page = 1;

                PageRequest pageRequest = PageRequest.of(
                                page - 1, limit,
                                Sort.by("id").descending());

                String extractedToken = authorizationHeader.substring(7);
                Student student = studentService.getDetailFromToken(extractedToken);

                Page<File> filePage = fileService.getAllFiles(student.getStudentId(), pageRequest);

                List<FileDetailResponse> fileResponseList = filePage.getContent().stream()
                                .map(FileDetailResponse::fromFile)
                                .toList();

                return ResponseEntity.ok().body(ResponseObject.builder()
                                .message("Get all of files successfully")
                                .status(HttpStatus.OK)
                                .data(fileResponseList)
                                .build());
        }

        @DeleteMapping("/{file-id}")
        @PreAuthorize("hasRole('STUDENT')")
        public ResponseEntity<ResponseObject> deleteFile(@PathVariable(value = "file-id") String fileId)
                        throws Exception {
                fileService.deleteFile(Integer.parseInt(fileId));
                return ResponseEntity.ok().body(ResponseObject.builder()
                                .message("Delete file successfully")
                                .status(HttpStatus.OK)
                                .data(null)
                                .build());
        }

        @GetMapping("/{file-id}")
        @PreAuthorize("hasRole('STUDENT')")
        public ResponseEntity<ResponseObject> getFile(@PathVariable(value = "file-id") String fileId) throws Exception {
                File file = fileService.getFileById(Integer.parseInt(fileId));
                FileDetailResponse fileResponse = FileDetailResponse.builder()
                                .id(file.getId())
                                .size(file.getSize())
                                .name(file.getName())
                                .url(file.getUrl())
                                .uploadDate(file.getUploadDate())
                                .fileFormat(file.getFileFormat().getName())
                                .build();

                return ResponseEntity.ok().body(ResponseObject.builder()
                                .message("Get file successfully")
                                .status(HttpStatus.OK)
                                .data(fileResponse)
                                .build());
        }
}
