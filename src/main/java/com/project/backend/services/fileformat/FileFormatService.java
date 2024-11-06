package com.project.backend.services.fileformat;

import com.project.backend.exceptions.DataNotFoundException;
import com.project.backend.models.File;
import com.project.backend.models.FileFormat;
import com.project.backend.repositories.FileFormatRepository;
import com.project.backend.responses.ResponseObject;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileFormatService implements IFileFormatService{
    @Autowired
    private FileFormatRepository fileFormatRepository;
    @Override
    public ResponseObject getAllFileFormats() {
        ResponseObject response = new ResponseObject();
        try{
            List<FileFormat> fileTypeList = fileFormatRepository.findAll();

            response.setData(fileTypeList);
            response.setStatus(HttpStatus.OK);
            response.setMessage("Get all file format successfully");
        } catch (Exception e){
           response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
           response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseObject addFileFormat(FileFormat newFormat) {
        ResponseObject response = new ResponseObject();
        try{
            if(fileFormatRepository.existsByName(newFormat.getName())) throw new Exception("This file format already existed");
            fileFormatRepository.save(newFormat);

            response.setStatus(HttpStatus.CREATED);
            response.setMessage("Add new format successfully");
        } catch(Exception e){
            response.setStatus(HttpStatus.CONFLICT);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseObject deleteFileFormat(Integer fileFormatId) {
        ResponseObject response = new ResponseObject();
        try{
            if(!fileFormatRepository.existsById(fileFormatId)) throw new DataNotFoundException("File format not found");
            fileFormatRepository.deleteById(fileFormatId);

            response.setStatus(HttpStatus.OK);
            response.setMessage("Delete file format successfully");
        } catch(Exception e){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
