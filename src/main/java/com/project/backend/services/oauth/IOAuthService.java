package com.project.backend.services.oauth;

import java.util.Map;

public interface IOAuthService {
    String buildAuthorizationUri() throws Exception;
    Map<String, Object> getOAuthGoogleToken(String authorizationCode) throws Exception;

}