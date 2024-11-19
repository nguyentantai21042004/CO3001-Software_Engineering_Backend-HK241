package com.project.backend.controllers;

import com.project.backend.models.File;
import com.project.backend.responses.FileResponse;
import com.project.backend.responses.ResponseObject;
import com.project.backend.services.file.FileService;
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

        @PostMapping
        @PreAuthorize("hasRole'STUDENT'")
        public ResponseEntity<ResponseObject> uploadFile(@RequestParam MultipartFile file) throws Exception {
                File newFile = fileService.uploadFile(file);
                FileResponse fileResponse = FileResponse.builder()
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

        @GetMapping("/get-all/{user-id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'SPSO')")
        public ResponseEntity<ResponseObject> getAllFiles(@PathVariable(value = "user-id") String userId,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int limit) {
                if (page < 1)
                        page = 1;

                PageRequest pageRequest = PageRequest.of(
                                page - 1, limit,
                                Sort.by("id").ascending());

                Page<File> filePage = fileService.getAllFilesByUserId(Integer.parseInt(userId), pageRequest);

                List<FileResponse> fileResponseList = filePage.getContent().stream()
                                .map(FileResponse::fromFile)
                                .toList();

                return ResponseEntity.ok().body(ResponseObject.builder()
                                .message("Get all of files successfully")
                                .status(HttpStatus.OK)
                                .data(fileResponseList)
                                .build());
        }

        @DeleteMapping("/{file-id}")
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
        public ResponseEntity<ResponseObject> getFile(@PathVariable(value = "file-id") String fileId) throws Exception {
                File file = fileService.getFileById(Integer.parseInt(fileId));
                FileResponse fileResponse = FileResponse.builder()
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
