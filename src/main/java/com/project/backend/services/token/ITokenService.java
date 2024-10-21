package com.project.backend.services.token;

import com.project.backend.models.Student;
import com.project.backend.models.Token;

import java.util.Map;

public interface ITokenService {
    Token addToken(Student student, String token);
}
