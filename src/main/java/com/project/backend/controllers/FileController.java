package com.project.backend.controllers;

import com.project.backend.responses.ResponseObject;
import com.project.backend.services.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${api.prefix}/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    @PostMapping
    public ResponseEntity<ResponseObject> uploadFile(@RequestParam MultipartFile file){
        ResponseObject response = fileService.uploadFile(file);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
