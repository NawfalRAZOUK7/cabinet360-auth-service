package com.cabinet360.auth.controller;

import com.cabinet360.auth.dto.*;
import com.cabinet360.auth.dto.register.AbstractRegisterRequest;
import com.cabinet360.auth.dto.request.EmailVerificationRequest;
import com.cabinet360.auth.dto.request.ForgotPasswordRequest;
import com.cabinet360.auth.dto.request.PhoneVerificationRequest;
import com.cabinet360.auth.dto.request.ResetPasswordRequest;
import com.cabinet360.auth.security.JwtTokenUtils;
import com.cabinet360.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenUtils jwtTokenUtils;

    public AuthController(AuthService authService, JwtTokenUtils jwtTokenUtils) {
        this.authService = authService;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AbstractRegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String token = extractToken(request);
        return ResponseEntity.ok(authService.getCurrentUser(token));
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<ApiMessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiMessageResponse.of("Password reset successful");
    }

    @PostMapping("/reset-password/initiate")
    public ResponseEntity<ApiMessageResponse> initiateReset(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.initiatePasswordReset(request);
        return ApiMessageResponse.of("Reset instructions sent");
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiMessageResponse> verifyEmail(@RequestBody EmailVerificationRequest request) {
        authService.verifyEmail(request);
        return ApiMessageResponse.of("Email verified successfully");
    }

    @PostMapping("/verify-phone")
    public ResponseEntity<?> verifyPhone(@RequestBody @Valid PhoneVerificationRequest request) {
        authService.verifyPhoneCode(request);
        return ApiMessageResponse.of("Phone number verified successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiMessageResponse> logout(HttpServletRequest request) {
        String token = extractToken(request);
        String email = jwtTokenUtils.extractEmail(token);
        authService.logout(email);
        return ApiMessageResponse.of("Successfully logged out ✅");
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        throw new IllegalArgumentException("Missing or invalid Authorization header");
    }
}
