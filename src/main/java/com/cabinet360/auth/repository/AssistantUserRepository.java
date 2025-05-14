package com.cabinet360.auth.repository;

import com.cabinet360.auth.entity.AssistantUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssistantUserRepository extends JpaRepository<AssistantUser, Long> {

    Optional<AssistantUser> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<AssistantUser> findByPasswordResetToken(String token);

    Optional<AssistantUser> findByEmailVerificationToken(String token);

    // 🔍 Utilisé pour /unconfirmed?role=ASSISTANT
    List<AssistantUser> findByIsConfirmedFalse();
}
