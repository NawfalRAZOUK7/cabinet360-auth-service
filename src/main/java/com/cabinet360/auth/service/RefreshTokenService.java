package com.cabinet360.auth.service;

import com.cabinet360.auth.entity.AuthUser;
import com.cabinet360.auth.entity.RefreshToken;
import com.cabinet360.auth.exception.AuthException;
import com.cabinet360.auth.repository.RefreshTokenRepository;
import com.cabinet360.auth.util.UserUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository tokenRepo;
    private final UserUtils userUtils;

    @Value("${jwt.refresh.expiration:604800000}") // 7 days default in milliseconds
    private long refreshExpirationMs;

    public RefreshTokenService(RefreshTokenRepository tokenRepo, UserUtils userUtils) {
        this.tokenRepo = tokenRepo;
        this.userUtils = userUtils;
    }

    // ✅ Create and persist a new token for a given user
    public RefreshToken createToken(AuthUser user) {
        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUserEmail(user.getEmail());
        token.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));
        token.setRevoked(false);
        return tokenRepo.save(token);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return tokenRepo.findByToken(token);
    }

    public boolean validate(String token) {
        return findByToken(token)
                .filter(rt -> !rt.isRevoked() && rt.getExpiryDate().isAfter(Instant.now()))
                .isPresent();
    }

    public void revoke(String token) {
        findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            tokenRepo.save(rt);
        });
    }

    public void deleteByUser(String email) {
        tokenRepo.deleteByUserEmail(email);
    }

    // ✅ Return full AuthUser (e.g. DoctorUser) based on refresh token validity
    public AuthUser getUserIfValid(String token) {
        RefreshToken rt = findByToken(token)
                .filter(t -> !t.isRevoked() && t.getExpiryDate().isAfter(Instant.now()))
                .orElseThrow(() -> new AuthException("Invalid or expired refresh token"));

        AuthUser user = userUtils.findUserByEmail(rt.getUserEmail());
        if (user == null) throw new AuthException("User not found for token");

        return user;
    }
}
