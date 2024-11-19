package com.project.backend.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.backend.models.SPSO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SPSORepository extends JpaRepository<SPSO, Long> {
    Optional<SPSO> findByUsername(String username);

    Optional<SPSO> findByEmail(String email);

    @Query("SELECT s FROM SPSO s WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "s.phoneNumber LIKE CONCAT('%', :keyword, '%'))")
    Page<SPSO> findAll(@Param("keyword") String keyword, Pageable pageable);

}
