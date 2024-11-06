package com.project.backend.services.fileformat;

import com.project.backend.models.FileFormat;
import com.project.backend.responses.ResponseObject;

public interface IFileFormatService {
    public ResponseObject getAllFileFormats();
    public ResponseObject addFileFormat(FileFormat newFormat);
    public ResponseObject deleteFileFormat(Integer fileFormatId);
}
