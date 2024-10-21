package com.project.backend.controllers;

import com.project.backend.dataTranferObjects.StudentLoginDTO;
import com.project.backend.models.Student;
import com.project.backend.models.Token;
import com.project.backend.responses.LoginResponse;
import com.project.backend.responses.ResponseObject;
import com.project.backend.services.oauth.IOAuthService;
import com.project.backend.services.student.IStudentService;
import com.project.backend.services.token.ITokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
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
    private final ITokenService tokenService;

    @GetMapping("/custom-oauth-login")
    public ResponseEntity<ResponseObject> OAuthLogin(
            HttpServletResponse response
    ) throws Exception {
        String authorizationUri = oAuthService.buildAuthorizationUri();
        response.sendRedirect(authorizationUri);

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Redirecting to OAuth login page")
                .status(HttpStatus.OK)
                .data(null)
                .build());
    }

    @GetMapping("/custom-oauth-callback")
    public ResponseEntity<ResponseObject> OAuthCallBack(
            @RequestParam("code") String authorizationCode
    ) throws Exception {
        Map<String, Object> tokenDataOAuth = oAuthService.getOAuthGoogleToken(authorizationCode);
        StudentLoginDTO studentLoginDTO = studentService.getStudentLoginDTO(tokenDataOAuth);

        String token = studentService.login(studentLoginDTO);
        Student studentDetail =studentService.getStudentDetailsByExtractingToken(token);

        Token jwtToken = tokenService.addToken(studentDetail, token);

        LoginResponse loginResponse = LoginResponse.builder()
                .message("user.login.login_successfully")
                .token(jwtToken.getToken())
                .username(studentDetail.getUsername())
                .roles(studentDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .id(Long.valueOf(studentDetail.getStudentId()))
                .build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(loginResponse.getMessage())
                .data(loginResponse)
                .status(HttpStatus.OK)
                .build());
    }
}
