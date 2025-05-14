package com.cabinet360.auth.dto;

import com.cabinet360.auth.dto.response.TokenRefreshResponse;

public class AuthResponse {

    private final String email;
    private final String role;
    private final TokenRefreshResponse token;

    public AuthResponse(String accessToken, String refreshToken, String email, String role) {
        this.email = email;
        this.role = role;
        this.token = new TokenRefreshResponse(accessToken, refreshToken);
    }

    // Getters
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public TokenRefreshResponse getToken() { return token; }
}
