package com.project.backend.services.fileformat;

import com.project.backend.dataTranferObjects.FileFormatDTO;
import com.project.backend.exceptions.DataNotFoundException;
import com.project.backend.models.FileFormat;
import com.project.backend.repositories.FileFormatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FileFormatService implements IFileFormatService{
    @Autowired
    private FileFormatRepository fileFormatRepository;
    @Override
    public Page<FileFormat> getAllFileFormats(Pageable pageable) {
        return fileFormatRepository.findAll(pageable);
    }

    @Override
    public FileFormat addFileFormat(FileFormatDTO newFormatDTO) throws Exception {

        if(fileFormatRepository.existsByName(newFormatDTO.getName())) throw new Exception("This file format already existed");
        FileFormat newFileFormat =  FileFormat.builder()
                .name(newFormatDTO.getName())
                .build();

        return fileFormatRepository.save(newFileFormat);
    }

    @Override
    public void deleteFileFormat(Integer fileFormatId) throws Exception{

            if(!fileFormatRepository.existsById(fileFormatId)) throw new DataNotFoundException("File format not found");
            fileFormatRepository.deleteById(fileFormatId);
    }
}
