package com.project.backend.services.userDetail;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.backend.repositories.StudentRepository;
import com.project.backend.repositories.SPSORepository;
import com.project.backend.models.Student;
import com.project.backend.models.SPSO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;
    private final SPSORepository spsoRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Tìm trong bảng `Student`
        Student student = studentRepository.findByEmail(email).orElse(null);
        if (student != null) {
            return student;
        }

        // Tìm trong bảng `SPSO`
        SPSO spso = spsoRepository.findByEmail(email).orElse(null);
        if (spso != null) {
            return spso;
        }

        throw new UsernameNotFoundException("User not found with email or username: " + email);
    }
}
