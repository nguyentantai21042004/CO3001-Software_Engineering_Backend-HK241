package com.project.backend.controllers;

import com.project.backend.models.FileFormat;
import com.project.backend.responses.ResponseObject;
import com.project.backend.services.fileformat.FileFormatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/file-formats")
@RequiredArgsConstructor
public class FileFormatController {
    private final FileFormatService fileFormatService;

    @GetMapping
    public ResponseEntity<ResponseObject> getAllFormats() {
        ResponseObject response = fileFormatService.getAllFileFormats();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping
    public ResponseEntity<ResponseObject> addFormat(@RequestBody FileFormat format) {
        ResponseObject response = fileFormatService.addFileFormat(format);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteFormat(@PathVariable String id) {
        ResponseObject response = fileFormatService.deleteFileFormat(Integer.parseInt(id));
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
