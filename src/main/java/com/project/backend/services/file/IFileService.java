package com.project.backend.services.file;

import com.project.backend.responses.ResponseObject;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    public ResponseObject uploadFile(MultipartFile file);
    public ResponseObject getAllFilesByUserId(Integer userId);
    public ResponseObject deleteFile(Integer fileId);
    public ResponseObject getFileById(Integer fileId);
}
