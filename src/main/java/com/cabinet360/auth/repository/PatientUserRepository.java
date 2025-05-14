package com.cabinet360.auth.repository;

import com.cabinet360.auth.entity.PatientUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientUserRepository extends JpaRepository<PatientUser, Long> {

    Optional<PatientUser> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<PatientUser> findByPasswordResetToken(String token);

    Optional<PatientUser> findByEmailVerificationToken(String token);

    // 🔍 Utilisé pour /unconfirmed?role=PATIENT
    List<PatientUser> findByIsConfirmedFalse();

    Optional<PatientUser> findByPhoneNumber(String phoneNumber);
}
