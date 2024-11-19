package com.project.backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, length = 255)
    private String token;

    @Column(name = "refresh_token", nullable = false, length = 255)
    private String refreshToken;

    @Column(name = "token_type", nullable = false, length = 50)
    private String tokenType;

    @Column(name = "refresh_expiration_date")
    private LocalDateTime refreshExpirationDate;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "user_name", nullable = false, length = 255)
    private String userName;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "role")
    private String role;

    @Column(name = "is_mobile", columnDefinition = "TINYINT(1)")
    private boolean isMobile;
}
