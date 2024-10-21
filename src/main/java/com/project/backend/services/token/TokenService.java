package com.project.backend.services.token;

import com.project.backend.models.Student;
import com.project.backend.models.Token;
import com.project.backend.repositories.TokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TokenService implements ITokenService{
    private final TokenRepository tokenRepository;

//    @Value("${jwt.expiration}")
//    private long expiration;
//
//    @Value("${jwt.expiration-refresh-token}")
//    private long jwtRefreshExpirationInSeconds;

    private static final int MAX_TOKENS = 1;

    @Override
    public Token addToken(Student student, String token) {
        List<Token> userTokens = tokenRepository.findByStudent(student);
        int tokenCount = userTokens.size();

        // Xóa token cũ nhất nếu đã vượt quá số lượng token cho phép
        if (tokenCount >= MAX_TOKENS) {
            Token tokenToDelete = userTokens.get(0);
            tokenRepository.delete(tokenToDelete);
        }

        // Tính toán thời gian hết hạn cho token và refresh token
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(2592000);
        LocalDateTime refreshExpirationDateTime = LocalDateTime.now().plusSeconds(5184000);

        // Lấy role từ student (giả sử có getter getRole trong class Student)
        String role = student.getRole().getName();

        // Tạo token mới
        Token newToken = Token.builder()
                .student(student)
                .token(token)
                .revoked(false)
                .tokenType("Bearer")
                .role(role)  // Gán role cho token
                .expiresAt(expirationDateTime)
                .refreshToken(UUID.randomUUID().toString())
                .refreshExpirationDate(refreshExpirationDateTime)
                .build();

        tokenRepository.save(newToken);
        return newToken;
    }

}