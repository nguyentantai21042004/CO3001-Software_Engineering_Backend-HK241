package com.project.backend.mapper;

import com.project.backend.dataTranferObjects.FileDTO;
import com.project.backend.models.File;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class FileMapper {
    public abstract FileDTO fileToFileDTO(File file);
    public abstract List<FileDTO> fileListToFileDTOList(List<File> fileList);
}
