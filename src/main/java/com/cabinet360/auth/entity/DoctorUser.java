package com.cabinet360.auth.entity;

import com.cabinet360.auth.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
        name = "doctor_users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_doctor_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_doctor_medical_license", columnNames = "medical_license_number")
        }
)
public class DoctorUser extends AuthUser {

    @Column(nullable = true)
    private String speciality;

    @Column(length = 1000)
    private String bio;

    @Column(nullable = true)
    private String documentsUrl;

    @Column(nullable = true, unique = true)
    private String medicalLicenseNumber;

    @Column(name = "two_fa_secret")
    private String twoFASecret;

    @Column(name = "is_two_fa_enabled")
    private boolean isTwoFAEnabled = false;

    @Embedded
    @NotNull
    private CabinetFields cabinet;

    public DoctorUser() {
        this.role = Role.DOCTOR;
        this.requiresApproval = true;
        this.isApproved = false;
    }

    // Getters and Setters
    public String getSpeciality() { return speciality; }
    public void setSpeciality(String speciality) { this.speciality = speciality; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getDocumentsUrl() { return documentsUrl; }
    public void setDocumentsUrl(String documentsUrl) { this.documentsUrl = documentsUrl; }

    public String getMedicalLicenseNumber() { return medicalLicenseNumber; }
    public void setMedicalLicenseNumber(String medicalLicenseNumber) { this.medicalLicenseNumber = medicalLicenseNumber; }

    public CabinetFields getCabinet() { return cabinet; }
    public void setCabinet(CabinetFields cabinet) { this.cabinet = cabinet; }

    public String getTwoFASecret() { return twoFASecret; }
    public void setTwoFASecret(String twoFASecret) { this.twoFASecret = twoFASecret; }

    public boolean isTwoFAEnabled() { return isTwoFAEnabled; }
    public void setTwoFAEnabled(boolean isTwoFAEnabled) { this.isTwoFAEnabled = isTwoFAEnabled; }
}
