package com.cabinet360.auth.repository;

import com.cabinet360.auth.entity.DoctorUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorUserRepository extends JpaRepository<DoctorUser, Long> {

    Optional<DoctorUser> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<DoctorUser> findByPasswordResetToken(String token);

    Optional<DoctorUser> findByEmailVerificationToken(String token);

    // 🔍 Docteurs dont l'email n'est pas encore confirmé
    List<DoctorUser> findByIsConfirmedFalse();

    // 🔍 Docteurs en attente d'approbation admin
    List<DoctorUser> findByIsApprovedFalseAndRequiresApprovalTrue();

    // 🔍 Docteurs filtrés par confirmation
    List<DoctorUser> findByIsConfirmed(boolean confirmed);

    // 🔍 Docteurs filtrés par approbation
    List<DoctorUser> findByIsApproved(boolean approved);

    // 🔍 Docteurs filtrés par confirmation + approbation
    List<DoctorUser> findByIsConfirmedAndIsApproved(boolean confirmed, boolean approved);
}
