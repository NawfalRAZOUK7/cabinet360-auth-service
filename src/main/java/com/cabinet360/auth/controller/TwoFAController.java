package com.cabinet360.auth.controller;

import com.cabinet360.auth.dto.request.TwoFASetupRequest;
import com.cabinet360.auth.dto.request.TwoFAVerifyRequest;
import com.cabinet360.auth.entity.DoctorUser;
import com.cabinet360.auth.repository.DoctorUserRepository;
import com.cabinet360.auth.service.TwoFAService;
import com.cabinet360.auth.util.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/twofa")
public class TwoFAController {

    private final TwoFAService twoFAService;
    private final UserUtils userUtils;
    private final DoctorUserRepository doctorRepo;

    public TwoFAController(TwoFAService twoFAService, UserUtils userUtils, DoctorUserRepository doctorRepo) {
        this.twoFAService = twoFAService;
        this.userUtils = userUtils;
        this.doctorRepo = doctorRepo;
    }

    @PostMapping("/setup")
    public ResponseEntity<?> setup2FA(@RequestBody TwoFASetupRequest request, HttpServletRequest servletRequest) {
        DoctorUser doctor = (DoctorUser) userUtils.getCurrentUserFromRequest(servletRequest);

        String secret = twoFAService.generateSecretKey();
        String qrUrl = twoFAService.buildQrUrl(doctor.getEmail(), secret);
        String qrImage = twoFAService.getQrImageUrl(doctor.getEmail(), secret);

        // Persist secret and enable flag
        doctor.setTwoFASecret(secret);
        doctor.setTwoFAEnabled(true);
        doctorRepo.save(doctor);

        return ResponseEntity.ok().body(
                new Object() {
                    public final String secretKey = secret;
                    public final String otpAuthUrl = qrUrl;
                    public final String qrCodeImageUrl = qrImage;
                }
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify2FA(@RequestBody TwoFAVerifyRequest request) {
        boolean valid = twoFAService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok().body(
                new Object() {
                    public final boolean success = valid;
                    public final String message = valid ? "2FA verified ✅" : "Invalid code ❌";
                }
        );
    }
}
