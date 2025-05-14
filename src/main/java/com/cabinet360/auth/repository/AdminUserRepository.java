package com.cabinet360.auth.repository;

import com.cabinet360.auth.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<AdminUser> findByPasswordResetToken(String token);
}
