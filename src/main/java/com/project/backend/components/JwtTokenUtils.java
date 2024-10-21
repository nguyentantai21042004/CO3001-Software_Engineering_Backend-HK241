package com.project.backend.components;

import com.project.backend.models.Student;
import com.project.backend.models.Token;
import com.project.backend.repositories.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    @Value("${jwt.expiration}")
    private int expiration;  // JWT expiration time

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    @Value("${jwt.secretKey}")
    private String secretKey;

    private final TokenRepository tokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);

    // Extract a claim from JWT
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws Exception {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from OAuth token
    public Claims extractAllClaimsFromOAuthToken(String token) throws Exception {
        return Jwts.parserBuilder()
                .setSigningKeyResolver(new SigningKeyResolverAdapter() {
                    @Override
                    public Key resolveSigningKey(JwsHeader header, Claims claims) {
                        try {
                            return getGooglePublicKey(header.getKeyId());
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to get Google public key", e);
                        }
                    }
                })
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract all claims from JWT
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if token is expired
    public boolean isTokenExpired(String token) throws Exception {
        Date expirationDate = extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    // Fetch Google public key using key ID (kid)
    private PublicKey getGooglePublicKey(String kid) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String certsUri = "https://www.googleapis.com/oauth2/v3/certs";

        ResponseEntity<Map> response = restTemplate.getForEntity(certsUri, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List<Map<String, Object>> keys = (List<Map<String, Object>>) response.getBody().get("keys");

            for (Map<String, Object> key : keys) {
                if (kid.equals(key.get("kid"))) {
                    String n = (String) key.get("n");
                    String e = (String) key.get("e");

                    byte[] nBytes = Base64.getUrlDecoder().decode(n);
                    byte[] eBytes = Base64.getUrlDecoder().decode(e);

                    BigInteger modulus = new BigInteger(1, nBytes);
                    BigInteger exponent = new BigInteger(1, eBytes);

                    RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    return keyFactory.generatePublic(spec);
                }
            }
            throw new Exception("Public key not found for kid: " + kid);
        } else {
            throw new Exception("Failed to retrieve Google public keys");
        }
    }

    // Generate JWT token for a student
    public String generateToken(Student student) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        String subject = student.getEmail();  // Email as the subject

        // Add claims to the token
        claims.put("subject", subject);
        claims.put("userId", student.getStudentId());
        claims.put("role", student.getRole().getName());

        try {
            // Create JWT token
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))  // Set expiration time
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)  // Sign with key and algorithm
                    .compact();
        } catch (Exception e) {
            throw new Exception("Cannot create JWT token, error: " + e.getMessage());
        }
    }

    // Extract subject (email) from student
    private static String getSubject(Student student) {
        String subject = student.getEmail();
        return (subject == null || subject.isBlank()) ? student.getEmail() : subject;
    }

    // Extract subject from token
    public String getSubject(String token) throws Exception {
        return extractClaim(token, Claims::getSubject);
    }

    // Thêm phương thức getUserType để lấy thông tin role từ token
    public String getUserType(String token) throws Exception {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Get signing key from secret
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token, Student studentDetails) {
        try {
            // Extract subject (email) from token
            String subject = extractClaim(token, Claims::getSubject);

            // Kiểm tra token có tồn tại trong database hay không
            Token existingToken = tokenRepository.findByToken(token);
            if (existingToken == null || existingToken.isRevoked() || !studentDetails.isEnabled()) {
                return false;
            }

            // So sánh subject (email) từ token với email của Student
            return (subject.equals(studentDetails.getUsername())) && !isTokenExpired(token);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

}