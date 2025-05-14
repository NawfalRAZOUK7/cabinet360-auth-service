package com.cabinet360.auth.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private boolean revoked = false;

    // Constructors
    public RefreshToken() {}

    public RefreshToken(String token, String userEmail, Instant expiryDate, boolean revoked) {
        this.token = token;
        this.userEmail = userEmail;
        this.expiryDate = expiryDate;
        this.revoked = revoked;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Instant getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Instant expiryDate) { this.expiryDate = expiryDate; }

    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }
}
