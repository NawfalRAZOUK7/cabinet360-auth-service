package com.cabinet360.auth.entity;

import com.cabinet360.auth.enums.Gender;
import com.cabinet360.auth.enums.Role;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "patient_users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_patient_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_patient_national_id", columnNames = "national_id"),
                @UniqueConstraint(name = "uk_patient_phone", columnNames = "phone_number")
        }
)
public class PatientUser extends AuthUser {

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, unique = true)
    private String nationalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate birthday;

    @Column(name = "phone_verification_code")
    private String phoneVerificationCode;

    @Column(name = "phone_verified")
    private boolean phoneVerified = false;

    public PatientUser() {
        this.role = Role.PATIENT;
        this.requiresApproval = false;
        this.isApproved = true;
    }

    // Getters and Setters
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    public String getPhoneVerificationCode() { return phoneVerificationCode; }
    public void setPhoneVerificationCode(String code) { this.phoneVerificationCode = code; }

    public boolean isPhoneVerified() { return phoneVerified; }
    public void setPhoneVerified(boolean verified) { this.phoneVerified = verified; }

}
