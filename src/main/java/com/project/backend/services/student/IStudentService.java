package com.project.backend.services.student;

import com.project.backend.dataTranferObjects.StudentLoginDTO;
import com.project.backend.models.Student;

import java.util.Map;

public interface IStudentService {
    StudentLoginDTO getStudentLoginDTO(Map<String, Object> tokenDataOAuth) throws Exception;
    String login(StudentLoginDTO studentLoginDTO) throws Exception;
    Student getStudentDetailsByExtractingToken(String token) throws Exception;
}
