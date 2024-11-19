package com.project.backend.services.student;

import com.project.backend.components.JwtTokenUtils;
import com.project.backend.dataTranferObjects.StudentLoginDTO;
import com.project.backend.exceptions.ExpiredTokenException;
import com.project.backend.models.Role;
import com.project.backend.models.Student;
import com.project.backend.repositories.RoleRepository;
import com.project.backend.repositories.StudentRepository;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
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

    // private Integer defaultBalance = 10;

    // Giá trị mặc định cho mật khẩu nếu chưa có mật khẩu nào khác
    private String getWebPassword() {
        String defaultPassword = "@hcmut.edu.vn";
        return passwordEncoder.encode(defaultPassword);
    }

    @Override
    public StudentLoginDTO getStudentLoginDTO(Map<String, Object> tokenDataOAuth) throws Exception {
        String idToken = (String) tokenDataOAuth.get("id_token");
        Claims claims = jwtTokenUtil.extractAllClaimsFromOAuthToken(idToken);

        return StudentLoginDTO.builder()
                .email(claims.get("email", String.class))
                .name(claims.get("name", String.class))
                .givenName(claims.get("given_name", String.class))
                .familyName(claims.get("family_name", String.class))
                .picture(claims.get("picture", String.class))
                .locale(claims.get("locale", String.class))
                .build();
    }

    @Override
    public String login(StudentLoginDTO studentLoginDTO) throws Exception {
        Optional<Student> optionalStudent = studentRepository.findByEmail(studentLoginDTO.getEmail());
        Student existingStudent;
        Role existingRole = roleRepository.findByName(Role.STUDENT);

        if (existingRole == null) {
            throw new RuntimeException("Role STUDENT not found");
        }

        // Tạo mới Student nếu chưa tồn tại
        if (optionalStudent.isEmpty()) {
            Student newStudent = Student.builder()
                    .fullName(studentLoginDTO.getName())
                    .email(studentLoginDTO.getEmail())
                    .studentBalance(100) // Số dư mặc định
                    .role(existingRole)
                    .password(getWebPassword())
                    .joinDate(LocalDateTime.now())
                    .lastLogin(LocalDateTime.now())
                    .build();

            existingStudent = studentRepository.save(newStudent);
        } else {
            existingStudent = optionalStudent.get();
            existingStudent.setLastLogin(LocalDateTime.now());

            if (existingStudent.getRole() == null) {
                existingStudent.setRole(existingRole);
                studentRepository.save(existingStudent);
            }
        }

        // Xác thực người dùng
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                existingStudent.getEmail(),
                "@hcmut.edu.vn", // Sử dụng mật khẩu mặc định đã mã hóa
                existingStudent.getAuthorities());
        authenticationManager.authenticate(authenticationToken);

        // Tạo token JWT với thông tin cần thiết
        String jwtToken = jwtTokenUtil.generateToken(
                existingStudent.getStudentId(), // userId
                existingRole.getName(), // role
                existingStudent.getEmail() // userName (ưu tiên email)
        );

        return jwtToken;
    }

    @Override
    public Student getStudentDetailsByExtractingToken(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }

        String subject = jwtTokenUtil.getSubject(token);
        Optional<Student> student = studentRepository.findByEmail(subject);
        return student.orElseThrow(() -> new Exception("Student not found"));
    }
}