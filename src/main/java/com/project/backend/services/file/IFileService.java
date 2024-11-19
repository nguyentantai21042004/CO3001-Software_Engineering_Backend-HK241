package com.project.backend.services.file;

import com.project.backend.models.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    public File uploadFile(MultipartFile file) throws Exception;
    public Page<File> getAllFilesByUserId(Integer userId, Pageable pageable);
    public void deleteFile(Integer fileId) throws Exception;
    public File getFileById(Integer fileId) throws Exception;
}
