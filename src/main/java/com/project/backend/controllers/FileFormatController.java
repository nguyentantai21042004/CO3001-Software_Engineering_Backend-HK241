package com.project.backend.controllers;

import com.project.backend.dataTranferObjects.FileFormatDTO;
import com.project.backend.models.FileFormat;
import com.project.backend.responses.FileFormatResponse;
import com.project.backend.responses.ResponseObject;
import com.project.backend.services.fileformat.FileFormatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/file-formats")
@RequiredArgsConstructor
public class FileFormatController {
        private final FileFormatService fileFormatService;

        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN')")
        public ResponseEntity<ResponseObject> getAllFormats(@RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int limit) throws Exception {
                if (page < 1)
                        page = 1;

                PageRequest pageRequest = PageRequest.of(
                                page - 1, limit,
                                Sort.by("id").ascending());
                Page<FileFormat> fileFormatPage = fileFormatService.getAllFileFormats(pageRequest);
                List<FileFormatResponse> fileFormatResponses = fileFormatPage.getContent().stream()
                                .map(FileFormatResponse::fromFileFormat)
                                .toList();

                return ResponseEntity.ok().body(ResponseObject.builder()
                                .message("Get all file formats successfully")
                                .status(HttpStatus.OK)
                                .data(fileFormatResponses)
                                .build());
        }

        @PostMapping
        @PreAuthorize("hasAnyRole('ADMIN')")
        public ResponseEntity<ResponseObject> addFormat(@RequestBody FileFormatDTO formatDTO) throws Exception {
                FileFormat newFileFormat = fileFormatService.addFileFormat(formatDTO);
                FileFormatResponse fileFormatResponse = FileFormatResponse.builder().name(newFileFormat.getName())
                                .build();

                return ResponseEntity.ok().body(ResponseObject.builder()
                                .message("Create new file format successfully")
                                .status(HttpStatus.CREATED)
                                .data(fileFormatResponse)
                                .build());
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN')")
        public ResponseEntity<ResponseObject> deleteFormat(@PathVariable String id) throws Exception {
                fileFormatService.deleteFileFormat(Integer.parseInt(id));
                return ResponseEntity.ok().body(ResponseObject.builder()
                                .message("Delete file format successfully")
                                .status(HttpStatus.OK)
                                .data(null)
                                .build());
        }
}
