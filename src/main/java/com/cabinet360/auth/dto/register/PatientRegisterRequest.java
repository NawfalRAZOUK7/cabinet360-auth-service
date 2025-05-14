package com.cabinet360.auth.dto.register;

import com.cabinet360.auth.enums.Gender;
import com.cabinet360.auth.enums.Role;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class PatientRegisterRequest extends AbstractRegisterRequest {
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "National ID is required")
    private String nationalId;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Birthday is required")
    @Past(message = "Birthday must be in the past")
    private LocalDate birthday;

    @Override
    public Role getRole() {
        return Role.PATIENT;
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
}
