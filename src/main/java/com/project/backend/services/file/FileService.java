package com.project.backend.services.file;

import com.project.backend.exceptions.DataNotFoundException;
import com.project.backend.exceptions.InvalidParamException;
import com.project.backend.models.File;
import com.project.backend.models.FileFormat;
import com.project.backend.models.Student;
import com.project.backend.repositories.FileFormatRepository;
import com.project.backend.repositories.FileRepository;
import com.project.backend.services.firebase.FirebaseStorageService;
import com.project.backend.services.student.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FileService implements IFileService {
        private final FileFormatRepository fileFormatRepository;
        private final FileRepository fileRepository;
        private final FirebaseStorageService firebaseStorageService;
        private final StudentService studentService;

        @Override
        public File uploadFile(String token, MultipartFile file) throws Exception {

                // Get student who upload file
                Student student = studentService.getDetailFromToken(token);

                String fileName = file.getOriginalFilename();
                String fileSize = Long.valueOf(file.getSize()).toString();
                String fileFormatName;

                // Get format name from original file name
                if (fileName != null && fileName.contains(".")) {
                        fileFormatName = fileName.substring(fileName.lastIndexOf('.') + 1);
                } else
                        throw new InvalidParamException("Invalid file name");

                String fileUrl = firebaseStorageService.uploadFile(file);

                FileFormat fileFormat = fileFormatRepository.findByName(fileFormatName)
                                .orElseThrow(() -> new DataNotFoundException("File format not found"));
                LocalDateTime now = LocalDateTime.now();

                File uploadFile = File.builder()
                                .student(student)
                                .name(fileName)
                                .size(fileSize)
                                .fileFormat(fileFormat)
                                .url(fileUrl)
                                .uploadDate(now)
                                .build();

                return fileRepository.save(uploadFile);
        }

        @Override
        public Page<File> getAllFiles(String token, Pageable pageable) throws Exception {
                Student student = studentService.getDetailFromToken(token);
                return fileRepository.findAllFilesByStudentId(student.getStudentId(), pageable);
        }

        @Override
        public void deleteFile(Integer fileId) throws Exception {
                File file = fileRepository.findById(fileId)
                                .orElseThrow(() -> new DataNotFoundException("File not found"));
                String fileUrl = file.getUrl();
                // Find filepath in true format
                int index = fileUrl.indexOf("o/");
                int endIndex = fileUrl.indexOf('?');
                String filePath = fileUrl.substring(index + 2, endIndex);
                System.out.println(filePath);
                boolean checkDeleted = firebaseStorageService.deleteFile(filePath);
                if (!checkDeleted)
                        throw new Exception("Can not delete file");
                else
                        fileRepository.delete(file);
        }

        @Override
        public File getFileById(Integer fileId) throws Exception {
                return fileRepository.findById(fileId).orElseThrow(() -> new DataNotFoundException("File not found"));
        }
}
