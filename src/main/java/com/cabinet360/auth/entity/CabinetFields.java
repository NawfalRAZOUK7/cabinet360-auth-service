package com.cabinet360.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CabinetFields {

    @Column(nullable = true)
    private String officeAddress;

    @Column(nullable = true)
    private String fixedPhoneNumber;

    @Column(nullable = true)
    private String workSchedule;

    @Column(nullable = false)
    private boolean availability = true;

    public CabinetFields() {}

    // ✅ Add this full constructor
    public CabinetFields(String officeAddress, String fixedPhoneNumber, String workSchedule, boolean availability) {
        this.officeAddress = officeAddress;
        this.fixedPhoneNumber = fixedPhoneNumber;
        this.workSchedule = workSchedule;
        this.availability = availability;
    }

    // Getters and Setters
    public String getOfficeAddress() { return officeAddress; }
    public void setOfficeAddress(String officeAddress) { this.officeAddress = officeAddress; }

    public String getFixedPhoneNumber() { return fixedPhoneNumber; }
    public void setFixedPhoneNumber(String fixedPhoneNumber) { this.fixedPhoneNumber = fixedPhoneNumber; }

    public String getWorkSchedule() { return workSchedule; }
    public void setWorkSchedule(String workSchedule) { this.workSchedule = workSchedule; }

    public boolean isAvailability() { return availability; }
    public void setAvailability(boolean availability) { this.availability = availability; }
}
