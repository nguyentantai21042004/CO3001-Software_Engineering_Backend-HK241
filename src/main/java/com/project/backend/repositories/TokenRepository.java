package com.project.backend.repositories;

import com.project.backend.models.Student;
import com.project.backend.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByStudent(Student student);
    Token findByToken(String token);
}
