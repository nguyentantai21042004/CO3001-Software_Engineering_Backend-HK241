package com.project.backend.controllers;

import com.project.backend.dataTranferObjects.StudentLoginDTO;
import com.project.backend.responses.ResponseObject;
import com.project.backend.services.oauth.IOAuthService;
import com.project.backend.services.student.IStudentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
                String authorizationUri = oAuthService.buildAuthorizationUri();
                response.sendRedirect(authorizationUri);

                return ResponseEntity.ok().body(ResponseObject.builder()
                                .message("Redirecting to OAuth login page")
                                .status(HttpStatus.OK)
                                .data(null)
                                .build());
        }

        @GetMapping("/custom-oauth-callback")
        public void OAuthCallBack(
                        @RequestParam("code") String authorizationCode,
                        HttpServletResponse response) throws Exception {
                Map<String, Object> tokenDataOAuth = oAuthService.getOAuthGoogleToken(authorizationCode);
                StudentLoginDTO studentLoginDTO = studentService.getStudentLoginDTO(tokenDataOAuth);

                String jwtToken = studentService.login(studentLoginDTO);

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
                String frontendRedirectUrl = "http://localhost:3000/api/auth/callback/google?token=" + jwtToken;
                response.sendRedirect(frontendRedirectUrl);
        }
}
