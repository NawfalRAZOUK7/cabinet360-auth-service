package com.cabinet360.auth.entity;

import com.cabinet360.auth.enums.Role;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@MappedSuperclass
public abstract class AuthUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false, unique = true)
    protected String email;

    @Column(nullable = false)
    protected String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected Role role;

    @Column(nullable = false)
    protected boolean isConfirmed = false;

    @Column(nullable = false)
    protected boolean isActive = true;

    protected LocalDateTime lastLoginAt;
    protected LocalDateTime createdAt = LocalDateTime.now();

    // ✅ Shared fields
    protected String firstName;
    protected String lastName;

    // ✅ Admin approval workflow
    @Column(nullable = false)
    protected boolean requiresApproval = false;

    @Column(nullable = false)
    protected boolean isApproved = false;

    @Column
    protected LocalDateTime approvedAt;

    // ✅ Password Reset
    @Column(name = "password_reset_token")
    protected String passwordResetToken;

    @Column(name = "password_reset_token_expires_at")
    protected LocalDateTime passwordResetTokenExpiresAt;

    @Column(name = "email_verification_token")
    protected String emailVerificationToken;

    // 👉 Constructors
    public AuthUser() {}

    public AuthUser(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // 👉 Getters and Setters

    public Long getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isConfirmed() { return isConfirmed; }
    public void setConfirmed(boolean confirmed) { isConfirmed = confirmed; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public boolean isRequiresApproval() { return requiresApproval; }
    public void setRequiresApproval(boolean requiresApproval) { this.requiresApproval = requiresApproval; }

    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public String getPasswordResetToken() { return passwordResetToken; }
    public void setPasswordResetToken(String passwordResetToken) { this.passwordResetToken = passwordResetToken; }

    public LocalDateTime getPasswordResetTokenExpiresAt() { return passwordResetTokenExpiresAt; }
    public void setPasswordResetTokenExpiresAt(LocalDateTime passwordResetTokenExpiresAt) {
        this.passwordResetTokenExpiresAt = passwordResetTokenExpiresAt;
    }

    public String getEmailVerificationToken() { return emailVerificationToken; }
    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }

    // 🔐 Required by Spring Security

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // You can customize this if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // You can customize this if needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // You can customize this if needed
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }
}
