package com.project.backend.components;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
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
    private int expiration;

    @Value("${jwt.secretKey}")
    private String secretKey;

    // Generate token for any type of user
    public String generateToken(Integer userId, String role, String username) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);

        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new Exception("Cannot create JWT token, error: " + e.getMessage());
        }
    }

    // Extract role from token
    public String getRole(String token) throws Exception {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Validate token for any user
    public boolean validateToken(String token, Long userId, String role, String username) {
        try {
            String subject = extractClaim(token, Claims::getSubject);

            // // Fetch token from database and validate its status
            // List<Token> existingTokens = tokenRepository.findByUserId(userId);
            // if (existingTokens.isEmpty() || !tokenRole.equals(role)) {
            // return false;
            // }

            // Check if the token's user matches
            return (subject.equals(username))
                    && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // Extract any claim from JWT
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws Exception {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // JwtTokenUtils.java
    public String getSubject(String token) throws Exception {
        return extractClaim(token, Claims::getSubject);
    }

    // JwtTokenUtils.java
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            // Extract subject (username) from token
            String subject = extractClaim(token, Claims::getSubject);

            // // Kiểm tra token có tồn tại trong database và không bị thu hồi
            // List<Token> existingTokens =
            // tokenRepository.findByUserId(userDetails.getUsername());
            // if (existingTokens.isEmpty()) {
            // return false;
            // }

            // So sánh subject từ token với username của UserDetails
            return (subject.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
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

    public Claims extractAllClaimsFromOAuthToken(String token) throws Exception {
        return Jwts.parserBuilder()
                .setSigningKeyResolver(new SigningKeyResolverAdapter() {
                    @SuppressWarnings("rawtypes")
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

    // Get signing key from secret
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}