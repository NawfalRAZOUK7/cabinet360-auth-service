package com.cabinet360.auth.service;

import com.cabinet360.auth.dto.AuthResponse;
import com.cabinet360.auth.dto.LoginRequest;
import com.cabinet360.auth.dto.request.*;
import com.cabinet360.auth.dto.register.*;
import com.cabinet360.auth.entity.*;
import com.cabinet360.auth.exception.AuthException;
import com.cabinet360.auth.repository.*;
import com.cabinet360.auth.security.JwtTokenUtils;
import com.cabinet360.auth.util.UserUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired private UserUtils userUtils;
    @Autowired private DoctorUserRepository doctorRepo;
    @Autowired private AssistantUserRepository assistantRepo;
    @Autowired private PatientUserRepository patientRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenUtils jwtTokenUtils;
    @Autowired private JavaMailSender mailSender;
    @Autowired private TwoFAService twoFAService;
    @Autowired private RefreshTokenService refreshTokenService;

    @Value("${dev.mode:false}")
    private boolean devMode;

    public AuthResponse login(LoginRequest request) {
        AuthUser user = userUtils.findUserByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid credentials");
        }

        if (user instanceof DoctorUser doctor && doctor.isTwoFAEnabled()) {
            if (request.getTwoFACode() == null) throw new AuthException("2FA code is required");
            try {
                int code = Integer.parseInt(request.getTwoFACode());
                if (!twoFAService.verifyCode(doctor.getTwoFASecret(), code)) {
                    throw new AuthException("Invalid 2FA code");
                }
            } catch (NumberFormatException e) {
                throw new AuthException("2FA code must be a valid number");
            }
        }

        user.setLastLoginAt(LocalDateTime.now());
        userUtils.saveUser(user);

        String accessToken = jwtTokenUtils.generateToken(user);
        String refreshToken = refreshTokenService.createToken(user).getToken();

        return new AuthResponse(accessToken, refreshToken, user.getEmail(), user.getRole().name());
    }

    public AuthUser getCurrentUser(String token) {
        String email = jwtTokenUtils.extractEmail(token);
        AuthUser user = userUtils.findUserByEmail(email);
        if (user == null) throw new AuthException("User not found");
        return user;
    }

    public AuthResponse register(AbstractRegisterRequest request) {
        if (request instanceof DoctorRegisterRequest doctorReq) return registerDoctor(doctorReq);
        else if (request instanceof AssistantRegisterRequest assistantReq) return registerAssistant(assistantReq);
        else if (request instanceof PatientRegisterRequest patientReq) return registerPatient(patientReq);
        else throw new IllegalArgumentException("Unknown registration type");
    }

    private AuthResponse registerDoctor(DoctorRegisterRequest req) {
        if (doctorRepo.existsByEmail(req.getEmail())) throw new AuthException("Email already used");

        DoctorUser doctor = new DoctorUser();
        doctor.setEmail(req.getEmail());
        doctor.setPassword(passwordEncoder.encode(req.getPassword()));
        doctor.setFirstName(req.getFirstName());
        doctor.setLastName(req.getLastName());
        doctor.setSpeciality(req.getSpeciality());
        doctor.setBio(req.getBio());
        doctor.setDocumentsUrl(req.getDocumentsUrl());
        doctor.setMedicalLicenseNumber(req.getMedicalLicenseNumber());
        doctor.setApproved(false);
        doctor.setRequiresApproval(true);

        doctor.setCabinet(new CabinetFields(
                req.getOfficeAddress(),
                req.getFixedPhoneNumber(),
                req.getWorkSchedule(),
                true
        ));

        String token = generateAndSetEmailVerificationToken(doctor);
        doctorRepo.save(doctor);
        sendVerificationEmail(doctor.getEmail(), token);

        return buildAuthResponse(doctor);
    }

    private AuthResponse registerAssistant(AssistantRegisterRequest req) {
        if (assistantRepo.existsByEmail(req.getEmail())) throw new AuthException("Email already used");

        AssistantUser assistant = new AssistantUser();
        assistant.setEmail(req.getEmail());
        assistant.setPassword(passwordEncoder.encode(req.getPassword()));
        assistant.setFirstName(req.getFirstName());
        assistant.setLastName(req.getLastName());
        assistant.setSupervisorId(req.getSupervisorId());
        assistant.setApproved(false);
        assistant.setRequiresApproval(true);

        assistant.setCabinet(new CabinetFields(
                req.getOfficeAddress(),
                req.getFixedPhoneNumber(),
                req.getWorkSchedule(),
                true
        ));

        String token = generateAndSetEmailVerificationToken(assistant);
        assistantRepo.save(assistant);
        sendVerificationEmail(assistant.getEmail(), token);

        return buildAuthResponse(assistant);
    }

    private AuthResponse registerPatient(PatientRegisterRequest req) {
        if (patientRepo.existsByEmail(req.getEmail())) throw new AuthException("Email already used");

        PatientUser patient = new PatientUser();
        patient.setEmail(req.getEmail());
        patient.setPassword(passwordEncoder.encode(req.getPassword()));
        patient.setFirstName(req.getFirstName());
        patient.setLastName(req.getLastName());
        patient.setAddress(req.getAddress());
        patient.setGender(req.getGender());
        patient.setBirthday(req.getBirthday());
        patient.setNationalId(req.getNationalId());
        patient.setPhoneNumber(req.getPhoneNumber());

        String emailToken = generateAndSetEmailVerificationToken(patient);
        String phoneCode = generatePhoneVerificationCode();
        patient.setPhoneVerificationCode(phoneCode);
        patient.setPhoneVerified(false);

        patientRepo.save(patient);
        sendVerificationEmail(patient.getEmail(), emailToken);

        logDev("📞", "Phone Code", phoneCode);
        return buildAuthResponse(patient);
    }

    private AuthResponse buildAuthResponse(AuthUser user) {
        String accessToken = jwtTokenUtils.generateToken(user);
        String refreshToken = refreshTokenService.createToken(user).getToken();
        return new AuthResponse(accessToken, refreshToken, user.getEmail(), user.getRole().name());
    }

    public void initiatePasswordReset(ForgotPasswordRequest request) {
        AuthUser user = userUtils.findUserByEmail(request.getEmail());
        if (user == null) throw new AuthException("Email not found");

        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiresAt(LocalDateTime.now());
        userUtils.saveUser(user);

        String html = "<h2>Password Reset Request</h2>" +
                "<p>Use the following token to reset your password:</p>" +
                "<div style='font-size:18px; font-weight:bold; margin:10px 0;'>" + token + "</div>" +
                "<p>If you didn’t request this, please ignore it.</p>";

        sendEmail(request.getEmail(), "Password Reset - Cabinet360", html);
        logDev("🔐", "Password Reset Token", token);
    }

    public void resetPassword(ResetPasswordRequest request) {
        AuthUser user = userUtils.findUserByResetToken(request.getResetToken());
        if (user == null) throw new AuthException("Invalid or expired token");

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiresAt(null);
        userUtils.saveUser(user);

        refreshTokenService.deleteByUser(user.getEmail());
        logDev("🔁", "Password Reset", "Refresh tokens revoked for " + user.getEmail());
    }

    public void verifyEmail(EmailVerificationRequest request) {
        AuthUser user = userUtils.findUserByVerificationToken(request.getToken());
        if (user == null) throw new AuthException("Invalid verification token");

        user.setConfirmed(true);
        user.setEmailVerificationToken(null);
        userUtils.saveUser(user);
    }

    public void verifyPhoneCode(PhoneVerificationRequest request) {
        PatientUser patient = patientRepo.findByPhoneNumber(request.getPhone())
                .orElseThrow(() -> new AuthException("Phone number not found"));

        if (!request.getCode().equals(patient.getPhoneVerificationCode())) {
            throw new AuthException("Invalid phone verification code");
        }

        patient.setPhoneVerified(true);
        patient.setPhoneVerificationCode(null);
        patientRepo.save(patient);

        logDev("✅", "Phone Verified", request.getPhone());
    }

    public void logout(String email) {
        refreshTokenService.deleteByUser(email);
        logDev("👋", "Logout", "Success for " + email);
    }

    // 🔧 UTILS

    private String generateAndSetEmailVerificationToken(AuthUser user) {
        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        user.setConfirmed(false);
        return token;
    }

    private String generatePhoneVerificationCode() {
        return String.valueOf((int)(100000 + Math.random() * 900000));
    }

    private void sendVerificationEmail(String to, String token) {
        String link = "http://localhost:8080/api/v1/auth/verify-email?token=" + token;
        String html = "<h2>Verify Your Email</h2><p>Click the link below:</p>" +
                "<a href=\"" + link + "\">Verify</a>";

        sendEmail(to, "Verify Email - Cabinet360", html);
        logDev("📧", "Email Verification Token", token);
    }

    private void sendEmail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Email sending failed", e);
        }
    }

    private void logDev(String emoji, String title, String message) {
        if (devMode) {
            System.out.println("🛠️ - " + emoji + " Dev Mode " + title + ": " + message);
        }
    }
}
