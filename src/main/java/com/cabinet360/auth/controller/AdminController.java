package com.cabinet360.auth.controller;

import com.cabinet360.auth.dto.ApproveDoctorRequest;
import com.cabinet360.auth.dto.DoctorResponseDTO;
import com.cabinet360.auth.dto.response.UserUnconfirmedDTO;
import com.cabinet360.auth.enums.Role;
import com.cabinet360.auth.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')") // 🔐 Protection par rôle ADMIN
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 🔹 1. Liste des utilisateurs non confirmés, filtrables par rôle et statut
    @GetMapping("/unconfirmed")
    public ResponseEntity<?> getUnconfirmedUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean confirmed,
            @RequestParam(required = false) Boolean approved
    ) {
        try {
            List<UserUnconfirmedDTO> result = adminService.getFilteredUnconfirmedUsers(role, confirmed, approved);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "error", e.getClass().getSimpleName(),
                            "message", e.getMessage()
                    )
            );
        }
    }


    // 🔹 2. Docteurs filtrés par confirmation + approbation
    @GetMapping("/doctors/filter")
    public ResponseEntity<List<DoctorResponseDTO>> filterDoctors(
            @RequestParam boolean confirmed,
            @RequestParam boolean approved) {
        return ResponseEntity.ok(adminService.filterDoctors(confirmed, approved));
    }

    // 🔹 3. Docteurs en attente de confirmation email
    @GetMapping("/doctors/pending-email")
    public ResponseEntity<List<DoctorResponseDTO>> getDoctorsPendingEmailConfirmation() {
        return ResponseEntity.ok(adminService.getDoctorsPendingEmailConfirmation());
    }

    // 🔹 4. Docteurs en attente d’approbation admin
    @GetMapping("/doctors/pending-approval")
    public ResponseEntity<List<DoctorResponseDTO>> getDoctorsPendingAdminApproval() {
        return ResponseEntity.ok(adminService.getDoctorsPendingAdminApproval());
    }

    // 🔹 5. Approbation par ID
    @PostMapping("/doctors/{id}/approve")
    public ResponseEntity<Void> approveDoctor(@PathVariable Long id) {
        adminService.approveDoctor(id);
        return ResponseEntity.ok().build();
    }

    // 🔹 6. Approbation via payload
    @PostMapping("/doctors/approve")
    public ResponseEntity<Void> approveDoctorViaPayload(@RequestBody ApproveDoctorRequest request) {
        adminService.approveDoctor(request);
        return ResponseEntity.ok().build();
    }
}
