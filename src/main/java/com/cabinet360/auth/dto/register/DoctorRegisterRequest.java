package com.cabinet360.auth.dto.register;

import com.cabinet360.auth.enums.Role;
import jakarta.validation.constraints.*;

public class DoctorRegisterRequest extends AbstractRegisterRequest {
    @NotBlank(message = "Speciality is required")
    private String speciality;

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;

    private String documentsUrl;

    @NotBlank(message = "Medical license number is required")
    private String medicalLicenseNumber;

    // Cabinet fields
    private String officeAddress;
    private String fixedPhoneNumber;
    private String workSchedule;

    @Override
    public Role getRole() {
        return Role.DOCTOR;
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

    public String getOfficeAddress() { return officeAddress; }
    public void setOfficeAddress(String officeAddress) { this.officeAddress = officeAddress; }

    public String getFixedPhoneNumber() { return fixedPhoneNumber; }
    public void setFixedPhoneNumber(String fixedPhoneNumber) { this.fixedPhoneNumber = fixedPhoneNumber; }

    public String getWorkSchedule() { return workSchedule; }
    public void setWorkSchedule(String workSchedule) { this.workSchedule = workSchedule; }
}
