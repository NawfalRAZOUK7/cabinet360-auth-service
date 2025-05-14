package com.cabinet360.auth.controller;

import com.cabinet360.auth.dto.request.*;
import com.cabinet360.auth.dto.response.*;
import com.cabinet360.auth.entity.*;
import com.cabinet360.auth.exception.AuthException;
import com.cabinet360.auth.security.*;
import com.cabinet360.auth.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

// 🔁 RefreshController (or add this in AuthController)
@RestController
@RequestMapping("/api/v1/auth")
public class TokenController {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenUtils jwtTokenUtils;

    public TokenController(RefreshTokenService refreshTokenService, JwtTokenUtils jwtTokenUtils) {
        this.refreshTokenService = refreshTokenService;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refresh(@RequestBody TokenRefreshRequest request) {
        RefreshToken oldToken = refreshTokenService.findByToken(request.getRefreshToken())
                .filter(token -> !token.isRevoked())
                .filter(token -> token.getExpiryDate().isAfter(Instant.now()))
                .orElseThrow(() -> new AuthException("Invalid or expired refresh token"));

        // 🔁 Step 1: Revoke the old token
        refreshTokenService.revoke(oldToken.getToken());

        // 🔁 Step 2: Get user and issue new tokens
        AuthUser user = refreshTokenService.getUserIfValid(oldToken.getToken());
        String newAccessToken = jwtTokenUtils.generateToken(user);
        RefreshToken newRefreshToken = refreshTokenService.createToken(user);

        // 🔁 Step 3: Return both tokens
        return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, newRefreshToken.getToken()));
    }

}
