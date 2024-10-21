package com.project.backend.services.oauth;

import com.project.backend.exceptions.DataNotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class OAuthService implements IOAuthService{
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    private String tokenUri;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String buildAuthorizationUri() throws Exception{
        String authorizationUri = "https://accounts.google.com/o/oauth2/auth" +
                "?client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&response_type=code" +
                "&scope=" + URLEncoder.encode("openid email profile", StandardCharsets.UTF_8);
        return authorizationUri;
    }

    @Override
    public Map<String, Object> getOAuthGoogleToken(String authorizationCode) throws Exception {
        HttpEntity<MultiValueMap<String, String>> request = getMultiValueMapHttpEntity(authorizationCode);

        ResponseEntity<Map> response;
        try {
            response = restTemplate.postForEntity(tokenUri, request, Map.class);
        } catch (HttpClientErrorException e) {
            throw new DataNotFoundException("Client error during OAuth communication: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            throw new DataNotFoundException("Server error from OAuth provider: " + e.getMessage());
        } catch (ResourceAccessException e) {
            throw new DataNotFoundException("Connection failed to OAuth provider: " + e.getMessage());
        }

        if (response.getBody() == null || response.getBody().isEmpty()) {
            throw new DataNotFoundException("No response from OAuth provider, please try again later");
        }
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new DataNotFoundException("Failed to communicate with OAuth provider, status: " + response.getStatusCode());
        }

        String accessToken = (String) response.getBody().get("access_token");
        String refreshToken = (String) response.getBody().get("refresh_token");
        String tokenType = (String) response.getBody().get("token_type");
        Integer expiresIn = (Integer) response.getBody().get("expires_in");
        String idToken = (String) response.getBody().get("id_token");

        // Trả về tất cả các giá trị trong một Map
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("access_token", accessToken);
        tokenData.put("refresh_token", refreshToken);
        tokenData.put("token_type", tokenType);
        tokenData.put("expires_in", expiresIn);
        tokenData.put("id_token", idToken);

        return tokenData;
    }


    private HttpEntity<MultiValueMap<String, String>> getMultiValueMapHttpEntity(String authorizationCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(OAuth2ParameterNames.CLIENT_ID, clientId);
        params.add(OAuth2ParameterNames.CLIENT_SECRET, clientSecret);
        params.add(OAuth2ParameterNames.CODE, authorizationCode);
        params.add(OAuth2ParameterNames.REDIRECT_URI, redirectUri);
        params.add(OAuth2ParameterNames.GRANT_TYPE, "authorization_code");

        return new HttpEntity<>(params, new HttpHeaders());
    }
}