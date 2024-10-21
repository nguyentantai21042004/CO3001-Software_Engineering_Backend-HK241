package com.project.backend.services.studentdetail;

import org.springframework.security.core.userdetails.UserDetails;

public interface IStudentDetailsService {
    UserDetails loadUserByUsername(String email) throws Exception;
}
