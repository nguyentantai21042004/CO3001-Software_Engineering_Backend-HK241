package com.project.backend.services.student;

import com.project.backend.components.JwtTokenUtils;
import com.project.backend.dataTranferObjects.StudentLoginDTO;
import com.project.backend.exceptions.DataNotFoundException;
import com.project.backend.exceptions.ExpiredTokenException;
import com.project.backend.exceptions.JWTException;
import com.project.backend.models.Role;
import com.project.backend.models.Student;
import com.project.backend.repositories.RoleRepository;
import com.project.backend.repositories.StudentRepository;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StudentService implements IStudentService {
    private final JwtTokenUtils jwtTokenUtil;
    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private String getWebPassword() {
        String defaultPassword = "@hcmut.edu.vn";
        return passwordEncoder.encode(defaultPassword);
    }

    @Override
    public StudentLoginDTO createDTO(Map<String, Object> tokenDataOAuth) throws Exception {
        String idToken = (String) tokenDataOAuth.get("id_token");
        if (idToken == null) {
            throw new JWTException("id_token is null");
        }

        Claims claims = jwtTokenUtil.extractAllClaimsFromOAuthToken(idToken);
        if (claims == null) {
            throw new JWTException("Claims is null");
        }

        if (!claims.get("email", String.class).contains("@hcmut.edu.vn")) {
            throw new JWTException("Email is not valid");
        }

        return StudentLoginDTO.builder()
                .email(claims.get("email", String.class))
                .name(claims.get("name", String.class))
                .givenName(claims.get("given_name", String.class))
                .familyName(claims.get("family_name", String.class))
                .picture(claims.get("picture", String.class))
                .build();
    }

    @Override
    public String getJWTToken(StudentLoginDTO studentLoginDTO) throws Exception {
        Optional<Student> optionalStudent = studentRepository.findByEmail(studentLoginDTO.getEmail());
        Student existingStudent = null;

        Role existingRole = roleRepository.findByName(Role.STUDENT);
        if (existingRole == null) {
            throw new DataNotFoundException("Role STUDENT not found");
        }

        if (optionalStudent.isEmpty()) {
            Student newStudent = Student.builder()
                    .fullName(studentLoginDTO.getName())
                    .email(studentLoginDTO.getEmail())
                    .studentBalance(0)
                    .role(existingRole)
                    .password(getWebPassword())
                    .joinDate(LocalDateTime.now())
                    .lastLogin(LocalDateTime.now())
                    .imageUrl(studentLoginDTO.getPicture())
                    .build();

            existingStudent = studentRepository.save(newStudent);
        } else {
            existingStudent = optionalStudent.get();
            existingStudent.setLastLogin(LocalDateTime.now());

            if (existingStudent.getRole() == null) {
                existingStudent.setRole(existingRole);
            }
            existingStudent.setImageUrl(studentLoginDTO.getPicture());

            studentRepository.save(existingStudent);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                existingStudent.getEmail(),
                "@hcmut.edu.vn",
                existingStudent.getAuthorities());
        authenticationManager.authenticate(authenticationToken);

        String jwtToken = jwtTokenUtil.generateToken(
                existingStudent.getStudentId(),
                existingRole.getName(),
                existingStudent.getEmail());

        return jwtToken;
    }

    @Override
    public Student getDetailFromToken(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }

        String subject = jwtTokenUtil.getSubject(token);
        if (subject == null) {
            throw new JWTException("Something went wrong in JWT extraction");
        }

        Optional<Student> student = studentRepository.findByEmail(subject);
        if (student.isEmpty()) {
            throw new DataNotFoundException("Student not found");
        }

        return student.get();
    }

    @Override
    public Page<Student> findAll(PageRequest pageRequest, String keyword) throws Exception {
        return studentRepository.findAll(pageRequest, keyword);
    }
}