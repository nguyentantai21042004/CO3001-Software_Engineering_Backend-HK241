package com.project.backend.controllers;

import com.project.backend.responses.ResponseObject;
import com.project.backend.services.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/get-all/{user-id}")
    public ResponseEntity<ResponseObject> getAllFiles(@PathVariable(value = "user-id") String userId){
        ResponseObject response = fileService.getAllFilesByUserId(Integer.parseInt(userId));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{file-id}")
    public ResponseEntity<ResponseObject> deleteFile(@PathVariable(value = "file-id") String fileId){
        ResponseObject response = fileService.deleteFile(Integer.parseInt(fileId));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{file-id}")
    public ResponseEntity<ResponseObject> getFile(@PathVariable(value = "file-id") String fileId){
        ResponseObject response = fileService.getFileById(Integer.parseInt(fileId));
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
