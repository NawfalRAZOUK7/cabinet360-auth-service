package com.cabinet360.auth.service;

import com.cabinet360.auth.dto.DoctorResponseDTO;
import com.cabinet360.auth.dto.ApproveDoctorRequest;
import com.cabinet360.auth.dto.response.UserUnconfirmedDTO;
import com.cabinet360.auth.entity.*;
import com.cabinet360.auth.enums.Role;
import com.cabinet360.auth.exception.AuthException;
import com.cabinet360.auth.mapper.DoctorMapper;
import com.cabinet360.auth.mapper.UserUnconfirmedMapper;
import com.cabinet360.auth.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    private final DoctorUserRepository doctorRepo;
    private final AssistantUserRepository assistantRepo;
    private final PatientUserRepository patientRepo;
    private final AdminUserRepository adminRepo;
    private final DoctorMapper doctorMapper;
    private final UserUnconfirmedMapper userUnconfirmedMapper;

    public AdminService(
            DoctorUserRepository doctorRepo,
            AssistantUserRepository assistantRepo,
            PatientUserRepository patientRepo,
            AdminUserRepository adminRepo,
            DoctorMapper doctorMapper,
            UserUnconfirmedMapper userUnconfirmedMapper
    ) {
        this.doctorRepo = doctorRepo;
        this.assistantRepo = assistantRepo;
        this.patientRepo = patientRepo;
        this.adminRepo = adminRepo;
        this.doctorMapper = doctorMapper;
        this.userUnconfirmedMapper = userUnconfirmedMapper;
    }

    // ✅ 1. Unconfirmed users filtered by role, confirmed, approved
    public List<UserUnconfirmedDTO> getFilteredUnconfirmedUsers(Role role, Boolean confirmed, Boolean approved) {
        List<AuthUser> result = new ArrayList<>();

        // 🔍 Doctor filtering: can use repository filtering when both confirmed & approved are provided
        if (role == null || role == Role.DOCTOR) {
            List<DoctorUser> doctors;

            if (confirmed != null && approved != null) {
                doctors = doctorRepo.findByIsConfirmedAndIsApproved(confirmed, approved);
            } else if (confirmed != null) {
                doctors = doctorRepo.findByIsConfirmed(confirmed);
            } else if (approved != null) {
                doctors = doctorRepo.findByIsApproved(approved);
            } else {
                doctors = doctorRepo.findAll();
            }

            result.addAll(doctors);
        }

        // 🔍 Assistant filtering
        if (role == null || role == Role.ASSISTANT) {
            if (confirmed == null || !confirmed) {
                result.addAll(assistantRepo.findByIsConfirmedFalse());
            } else {
                result.addAll(assistantRepo.findAll().stream().filter(AuthUser::isConfirmed).toList());
            }
        }

        // 🔍 Patient filtering
        if (role == null || role == Role.PATIENT) {
            if (confirmed == null || !confirmed) {
                result.addAll(patientRepo.findByIsConfirmedFalse());
            } else {
                result.addAll(patientRepo.findAll().stream().filter(AuthUser::isConfirmed).toList());
            }
        }

        return userUnconfirmedMapper.toDtoList(result);
    }

    // 🔍 2. Filter doctors by confirmed + approved
    public List<DoctorResponseDTO> filterDoctors(boolean confirmed, boolean approved) {
        return doctorRepo.findByIsConfirmedAndIsApproved(confirmed, approved)
                .stream()
                .map(doctorMapper::toDto)
                .toList();
    }

    // 🔍 3. Doctors with unconfirmed emails
    public List<DoctorResponseDTO> getDoctorsPendingEmailConfirmation() {
        return doctorRepo.findByIsConfirmedFalse()
                .stream()
                .map(doctorMapper::toDto)
                .toList();
    }

    // 🔍 4. Doctors requiring admin approval
    public List<DoctorResponseDTO> getDoctorsPendingAdminApproval() {
        return doctorRepo.findByIsApprovedFalseAndRequiresApprovalTrue()
                .stream()
                .map(doctorMapper::toDto)
                .toList();
    }

    // ✅ 5. Approve doctor by ID
    public void approveDoctor(Long id) {
        DoctorUser doctor = doctorRepo.findById(id)
                .orElseThrow(() -> new AuthException("Doctor not found"));
        doctor.setApproved(true);
        doctorRepo.save(doctor);
    }

    // ✅ 6. Approve doctor via payload
    public void approveDoctor(ApproveDoctorRequest request) {
        approveDoctor(request.getDoctorId());
    }
}
