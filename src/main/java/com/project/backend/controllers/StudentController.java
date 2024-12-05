package com.project.backend.controllers;

import com.project.backend.dataTranferObjects.StudentLoginDTO;
import com.project.backend.exceptions.DataNotFoundException;
import com.project.backend.exceptions.JWTException;
import com.project.backend.models.Student;
import com.project.backend.responses.ResponseObject;
import com.project.backend.responses.students.StudentDetailResponse;
import com.project.backend.responses.students.StudentResponse;
import com.project.backend.services.oauth.IOAuthService;
import com.project.backend.services.student.IStudentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.project.backend.responses.students.StudentListResponse;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class StudentController {
        private final IOAuthService oAuthService;
        private final IStudentService studentService;

        @GetMapping("/custom-oauth-login")
        public ResponseEntity<ResponseObject> OAuthLogin(
                        HttpServletResponse response) throws Exception {
                try {
                        String authorizationUri = oAuthService.buildAuthorizationUri();
                        response.sendRedirect(authorizationUri);

                        return ResponseEntity.ok().body(ResponseObject.builder()
                                        .message("Redirecting to OAuth login page")
                                        .status(HttpStatus.OK)
                                        .data(null)
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .data(null)
                                        .build());
                }
        }

        @GetMapping("/custom-oauth-callback")
        public ResponseEntity<ResponseObject> OAuthCallBack(
                        @RequestParam("code") String authorizationCode,
                        HttpServletResponse response) throws Exception {
                try {
                        Map<String, Object> tokenDataOAuth = oAuthService.getOAuthGoogleToken(authorizationCode);
                        StudentLoginDTO studentLoginDTO = studentService.createDTO(tokenDataOAuth);

                        String jwtToken = studentService.getJWTToken(studentLoginDTO);

                        System.out.println(jwtToken);

                        // LoginResponse loginResponse = LoginResponse.builder()
                        // .message("user.login.login_successfully")
                        // .token(jwtToken)
                        // .tokenType("Bearer")
                        // .build();

                        // return ResponseEntity.ok().body(ResponseObject.builder()
                        // .message(loginResponse.getMessage())
                        // .data(loginResponse)
                        // .status(HttpStatus.OK)
                        // .build());

                        // 3. Redirect người dùng về frontend, kèm token
                        String frontendRedirectUrl = "https://bkprinter.vercel.app/api/auth/callback/google?token="
                                        + jwtToken;
                        response.sendRedirect(frontendRedirectUrl);

                        return ResponseEntity.ok().body(ResponseObject.builder()
                                        .message("Redirecting to frontend")
                                        .status(HttpStatus.OK)
                                        .data(null)
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .data(null)
                                        .build());
                }
        }

        @GetMapping("/get")
        @PreAuthorize("hasRole('ADMIN') or hasRole('SPSO')")
        public ResponseEntity<ResponseObject> Get(
                        @RequestParam(defaultValue = "", required = false) String keyword,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int limit) throws Exception {
                if (page < 1 || limit < 1) {
                        throw new BadRequestException("Page and limit must be greater than 0");
                }
                PageRequest pageRequest = PageRequest.of(
                                page - 1, limit,
                                Sort.by("id").ascending());

                Page<Student> students = studentService.findAll(pageRequest, keyword);
                Page<StudentResponse> studentResponses = students.map(StudentResponse::fromStudent);

                int totalPages = students.getTotalPages();

                StudentListResponse studentListResponse = StudentListResponse.builder()
                                .students(studentResponses.getContent())
                                .currentPage(page)
                                .itemsPerPage(limit)
                                .totalPages(totalPages)
                                .build();

                return ResponseEntity.ok(ResponseObject.builder()
                                .data(studentListResponse)
                                .message("Students fetched successfully")
                                .status(HttpStatus.OK)
                                .build());
        }

        @GetMapping("/detail")
        @PreAuthorize("hasRole('STUDENT')")
        public ResponseEntity<ResponseObject> Detail(
                        @RequestHeader("Authorization") String authorizationHeader) throws Exception {
                try {
                        String token = authorizationHeader.substring(7);
                        Student student = studentService.getDetailFromToken(token);

                        return ResponseEntity.ok(ResponseObject.builder()
                                        .data(StudentDetailResponse.fromStudent(student))
                                        .message("Student details fetched successfully")
                                        .status(HttpStatus.OK)
                                        .build());
                } catch (JWTException e) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.UNAUTHORIZED)
                                        .data(null)
                                        .build());
                } catch (DataNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.NOT_FOUND)
                                        .data(null)
                                        .build());
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                                        .message(e.getMessage())
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .data(null)
                                        .build());
                }
        }
}
