package com.project.backend.services.admin;

import com.project.backend.components.JwtTokenUtils;
import com.project.backend.dataTranferObjects.AdminLoginDTO;
import com.project.backend.dataTranferObjects.SPSOCreateDTO;
import com.project.backend.dataTranferObjects.UpdateUserDTO;
import com.project.backend.exceptions.ExpiredTokenException;
import com.project.backend.models.Role;
import com.project.backend.models.SPSO;
import com.project.backend.repositories.RoleRepository;
import com.project.backend.repositories.SPSORepository;
import com.project.backend.dataTranferObjects.UpdateUserDTO;

import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService implements IAdminService {
    private final SPSORepository spsoRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<SPSO> findAll(String keyword, Pageable pageable) {
        return spsoRepository.findAll(keyword, pageable);
    }

    @Override
    public String login(AdminLoginDTO adminLoginDTO) throws Exception {
        // Tìm kiếm người dùng SPSO theo username
        Optional<SPSO> optionalSPSO = spsoRepository.findByUsername(adminLoginDTO.getUsername());
        if (!optionalSPSO.isPresent()) {
            throw new Exception("SPSO not found");
        }

        SPSO existingSPSO = optionalSPSO.get();

        // Xác minh mật khẩu
        if (!passwordEncoder.matches(adminLoginDTO.getPassword(), existingSPSO.getPassword())) {
            throw new Exception("Invalid password");
        }

        // Kiểm tra vai trò
        Role defaultRole = roleRepository.findByName(adminLoginDTO.getRole());
        if (defaultRole == null) {
            throw new Exception("Role not found");
        }

        // Xác thực với AuthenticationManager
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                existingSPSO.getEmail(),
                adminLoginDTO.getPassword(),
                existingSPSO.getAuthorities());
        authenticationManager.authenticate(authenticationToken);

        // Tạo JWT token
        String jwtToken = jwtTokenUtil.generateToken(
                existingSPSO.getId(), // userId
                defaultRole.getName(), // role
                existingSPSO.getEmail() // userName (ưu tiên email)
        );

        return jwtToken; // Trả về JWT
    }

    @Override
    public SPSO getOne(String username) {
        return spsoRepository.findByUsername(username).orElse(null);
    }

    @Override
    public SPSO createSPSO(SPSOCreateDTO spsoCreateDTO) throws Exception {
        Role existingRole = roleRepository.findByName("SPSO");
        if (existingRole == null) {
            throw new Exception("Role not found");
        }

        Optional<SPSO> existingSPSO;
        existingSPSO = spsoRepository.findByEmail(spsoCreateDTO.getEmail());
        if (existingSPSO.isPresent()) {
            throw new Exception("SPSO already exists");
        }
        existingSPSO = spsoRepository.findByUsername(spsoCreateDTO.getUsername());
        if (existingSPSO.isPresent()) {
            throw new Exception("SPSO username already exists");
        }

        SPSO newSPSO = SPSO.builder()
                .email(spsoCreateDTO.getEmail())
                .name(spsoCreateDTO.getUsername())
                .username(spsoCreateDTO.getUsername())
                .role(existingRole)
                .password(passwordEncoder.encode(spsoCreateDTO.getPassword()))
                .build();

        return spsoRepository.save(newSPSO);
    }

    @Override
    public SPSO updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception {
        SPSO existingSPSO = spsoRepository.findById(userId).orElseThrow(
                () -> new Exception("SPSO not found"));

        // existingSPSO.setName(updatedUserDTO.getName());
        // existingSPSO.setPhoneNumber(updatedUserDTO.getPhoneNumber());
        // existingSPSO.setRole(roleRepository.findByName(updatedUserDTO.getRole()));

        return spsoRepository.save(existingSPSO);
    }

    @Override
    public SPSO getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }
        String subject = jwtTokenUtil.getSubject(token);
        Optional<SPSO> user;
        user = spsoRepository.findByEmail(subject);
        if (user.isEmpty()) {
            throw new Exception("User not found");
        }
        return user.orElseThrow(() -> new Exception("User not found"));
    }
}
