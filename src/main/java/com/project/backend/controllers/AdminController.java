package com.project.backend.controllers;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.backend.responses.LoginResponse;
import com.project.backend.responses.ResponseObject;
import com.project.backend.responses.UserResponse;
import com.project.backend.dataTranferObjects.AdminLoginDTO;
import com.project.backend.dataTranferObjects.SPSOCreateDTO;
import com.project.backend.dataTranferObjects.UpdateUserDTO;
import com.project.backend.models.SPSO;
import com.project.backend.services.admin.IAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.prefix}/internal/admin")
@RequiredArgsConstructor
public class AdminController {
        private final IAdminService adminService;

        @GetMapping("/get")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ResponseObject> getSPSO(
                        @RequestParam(defaultValue = "", required = false) String keyword,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int limit) throws Exception {
                if (page < 1)
                        page = 1;

                PageRequest pageRequest = PageRequest.of(
                                page - 1, limit,
                                Sort.by("id").ascending());
                Page<SPSO> spsoPage = adminService.findAll(keyword, pageRequest);

                List<UserResponse> spsos = spsoPage.getContent().stream()
                                .map(UserResponse::fromSPSO)
                                .collect(Collectors.toList());

                return ResponseEntity.ok().body(ResponseObject.builder()
                                .message("admin.spso.get_successfully")
                                .status(HttpStatus.OK)
                                .data(spsos)
                                .build());
        }

        @PostMapping("/login")
        public ResponseEntity<ResponseObject> login(@Valid @RequestBody AdminLoginDTO adminLoginDTO,
                        BindingResult bindingResult) throws Exception {
                if (bindingResult.hasErrors()) {
                        return ResponseEntity.badRequest().body(ResponseObject.builder()
                                        .message("admin.login.invalid_credentials")
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }

                String jwtToken = adminService.login(adminLoginDTO);

                LoginResponse loginResponse = LoginResponse.builder()
                                .message("admin.login.login_successfully")
                                .token(jwtToken)
                                .tokenType("Bearer")
                                .build();

                return ResponseEntity.ok().body(ResponseObject.builder()
                                .message("admin.login.login_successfully")
                                .status(HttpStatus.OK)
                                .data(loginResponse)
                                .build());
        }

        @PostMapping("/spso/create")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ResponseObject> createSPSO(@Valid @RequestBody SPSOCreateDTO spsoCreateDTO,
                        BindingResult bindingResult) {
                if (bindingResult.hasErrors()) {
                        return ResponseEntity.badRequest().body(ResponseObject.builder()
                                        .message("admin.spso.create_failed")
                                        .status(HttpStatus.BAD_REQUEST)
                                        .data(null)
                                        .build());
                }

                SPSO newSPSO = adminService.createSPSO(spsoCreateDTO);
                return ResponseEntity.ok().body(ResponseObject.builder()
                                .message("admin.spso.create_successfully")
                                .status(HttpStatus.OK)
                                .data(UserResponse.fromSPSO(newSPSO))
                                .build());
        }

        @PutMapping("/detail/{user_id}")
        @PreAuthorize("hasRole('ADMIN') or hasRole('SPSO')")
        public ResponseEntity<ResponseObject> updateUserDetail(
                        @PathVariable("user_id") Long userId,
                        @RequestBody UpdateUserDTO updatedUserDTO,
                        @RequestHeader("Authorization") String authorizationHeader) throws Exception {
                String extractedToken = authorizationHeader.substring(7);
                SPSO user = adminService.getUserDetailsFromToken(extractedToken);

                // Ensure that the user making the request matches the user being
                if (user.getId().longValue() != userId)
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

                SPSO updatedUser = adminService.updateUser(userId, updatedUserDTO);
                return ResponseEntity.ok().body(ResponseObject.builder()
                                .message("Update user detail successfully")
                                .data(UserResponse.fromSPSO(updatedUser))
                                .status(HttpStatus.OK)
                                .build());
        }
}