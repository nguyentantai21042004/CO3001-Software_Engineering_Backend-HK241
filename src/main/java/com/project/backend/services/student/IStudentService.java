package com.project.backend.services.student;

import com.project.backend.dataTranferObjects.StudentLoginDTO;
import com.project.backend.models.Student;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IStudentService {
    StudentLoginDTO createDTO(Map<String, Object> tokenDataOAuth) throws Exception;

    String getJWTToken(StudentLoginDTO studentLoginDTO) throws Exception;

    Student getDetailFromToken(String token) throws Exception;

    Page<Student> findAll(PageRequest pageRequest, String keyword) throws Exception;
}
