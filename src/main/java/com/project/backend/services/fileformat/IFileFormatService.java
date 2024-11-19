package com.project.backend.services.fileformat;

import com.project.backend.dataTranferObjects.FileFormatDTO;
import com.project.backend.models.FileFormat;
import com.project.backend.responses.ResponseObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IFileFormatService {
    public Page<FileFormat> getAllFileFormats(Pageable pageable);
    public FileFormat addFileFormat(FileFormatDTO newFormatDTO) throws Exception;
    public void deleteFileFormat(Integer fileFormatId) throws Exception;
}
