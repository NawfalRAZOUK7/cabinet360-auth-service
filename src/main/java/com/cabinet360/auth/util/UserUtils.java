package com.cabinet360.auth.util;

import com.cabinet360.auth.entity.*;
import com.cabinet360.auth.repository.*;
import com.cabinet360.auth.security.JwtTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Stream;

@Component
public class UserUtils {

    private final DoctorUserRepository doctorRepo;
    private final AssistantUserRepository assistantRepo;
    private final PatientUserRepository patientRepo;
    private final AdminUserRepository adminRepo;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    public UserUtils(
            DoctorUserRepository doctorRepo,
            AssistantUserRepository assistantRepo,
            PatientUserRepository patientRepo,
            AdminUserRepository adminRepo
    ) {
        this.doctorRepo = doctorRepo;
        this.assistantRepo = assistantRepo;
        this.patientRepo = patientRepo;
        this.adminRepo = adminRepo;
    }

    public AuthUser findUserByEmail(String email) {
        return Stream.of(
                        doctorRepo.findByEmail(email),
                        assistantRepo.findByEmail(email),
                        patientRepo.findByEmail(email),
                        adminRepo.findByEmail(email)
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(u -> (AuthUser) u)
                .findFirst()
                .orElse(null);
    }

    public AuthUser findUserByResetToken(String token) {
        return Stream.of(
                        doctorRepo.findByPasswordResetToken(token),
                        assistantRepo.findByPasswordResetToken(token),
                        patientRepo.findByPasswordResetToken(token),
                        adminRepo.findByPasswordResetToken(token)
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(u -> (AuthUser) u)
                .findFirst()
                .orElse(null);
    }

    public AuthUser findUserByVerificationToken(String token) {
        return Stream.of(
                        doctorRepo.findByEmailVerificationToken(token),
                        assistantRepo.findByEmailVerificationToken(token),
                        patientRepo.findByEmailVerificationToken(token)
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(u -> (AuthUser) u)
                .findFirst()
                .orElse(null);
    }

    public AuthUser getCurrentUserFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            String token = bearer.substring(7);
            String email = jwtTokenUtils.extractEmail(token);
            return findUserByEmail(email);
        }
        throw new RuntimeException("Missing or invalid Authorization header");
    }

    public void saveUser(AuthUser user) {
        if (user instanceof DoctorUser doctor) doctorRepo.save(doctor);
        else if (user instanceof AssistantUser assistant) assistantRepo.save(assistant);
        else if (user instanceof PatientUser patient) patientRepo.save(patient);
        else if (user instanceof AdminUser admin) adminRepo.save(admin);
    }
}
