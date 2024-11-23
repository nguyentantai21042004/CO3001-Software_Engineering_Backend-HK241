package com.project.backend.services.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.backend.dataTranferObjects.AdminLoginDTO;
import com.project.backend.dataTranferObjects.SPSOCreateDTO;
import com.project.backend.dataTranferObjects.UpdateUserDTO;
import com.project.backend.models.SPSO;

public interface IAdminService {
    Page<SPSO> findAll(String keyword, Pageable pageable);

    String login(AdminLoginDTO adminLoginDTO) throws Exception;

    SPSO getOne(String username);

    SPSO getUserDetailsFromToken(String token) throws Exception;

    SPSO createSPSO(SPSOCreateDTO spsoCreateDTO) throws Exception;

    SPSO updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;

    SPSO findSpsoById(Long id);

}
